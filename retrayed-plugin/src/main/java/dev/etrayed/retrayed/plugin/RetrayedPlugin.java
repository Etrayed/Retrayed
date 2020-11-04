package dev.etrayed.retrayed.plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.etrayed.retrayed.api.PluginPurpose;
import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.api.RetrayedAPI;
import dev.etrayed.retrayed.plugin.event.EventIteratorFactory;
import dev.etrayed.retrayed.plugin.event.EventRegistry;
import dev.etrayed.retrayed.plugin.play.PlaybackImpl;
import dev.etrayed.retrayed.plugin.record.RecordingPacketListener;
import dev.etrayed.retrayed.plugin.record.TickCounter;
import dev.etrayed.retrayed.plugin.replay.PlayingReplay;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import dev.etrayed.retrayed.plugin.storage.ReplayStorage;
import dev.etrayed.retrayed.plugin.storage.StorageStrategy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

/**
 * @author Etrayed
 */
@Plugin(name = "Retrayed", version = "DEV")
@Author("Etrayed")
@DependsOn(@Dependency("ProtocolLib"))
public class RetrayedPlugin extends JavaPlugin implements IRetrayedPlugin {

    private ReplayStorage<?> replayStorage;

    private Replay replay;

    private PluginPurpose purpose;

    private ScheduledExecutorService executorService;

    private EventIteratorFactory eventIteratorFactory;

    private EventRegistry eventRegistry;

    private PlaybackImpl playback;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.executorService = Executors.newScheduledThreadPool(0, new ThreadFactoryBuilder()
                .setNameFormat("Retrayed-Worker-%d").setDaemon(true).build());

        try {
            this.replayStorage = StorageStrategy.fromString(getConfig().getString("storageStrategy"))
                    .createStorage(new ReplayStorage.Credentials(this.getConfig().getConfigurationSection("credentials")), this);
        } catch (ReflectiveOperationException e) {
            getLogger().log(Level.SEVERE, "Could not initialize replayStorage: ", e);

            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        Bukkit.getServicesManager().register(RetrayedAPI.class, this, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        try {
            registerListeners();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not register listeners: ", e);

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    private void registerListeners() throws IOException {
        Set<ClassPath.ClassInfo> classes = ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("dev.etrayed.retrayed.plugin.listener");

        for (ClassPath.ClassInfo classInfo : classes) {
            try {
                Class<?> listenerClass = Class.forName(classInfo.getName(), true, getClass().getClassLoader());

                if(Listener.class.isAssignableFrom(listenerClass) && !Modifier.isAbstract(listenerClass.getModifiers())) {
                    Constructor<? extends Listener> constructor = (Constructor<? extends Listener>) listenerClass
                            .getDeclaredConstructor(RetrayedPlugin.class);

                    constructor.setAccessible(true);

                    Bukkit.getPluginManager().registerEvents(constructor.newInstance(this), this);
                }
            } catch (ReflectiveOperationException e) {
                getLogger().log(Level.SEVERE, "Failed to register listener " + classInfo.getName() + ": ", e);
            }
        }
    }

    @Override
    public void onDisable() {
        if(replayStorage != null) {
            if(replay instanceof RecordingReplay) {
                replayStorage.save((RecordingReplay) replay);
            }

            try {
                replayStorage.close();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to close replayStorage: ", e);
            }
        }

        if(executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public Future<Replay> initReplay(int replayId, PluginPurpose purpose) {
        Preconditions.checkArgument(replay == null || purpose == null, "This server has already been initialized.");

        if(purpose == PluginPurpose.NONE) {
            Bukkit.getPluginManager().disablePlugin(this);

            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Replay> primaryFuture = initReplay0(replayId, purpose);
        CompletableFuture<Replay> secondaryFuture = new CompletableFuture<>();

        executorService.submit(() -> {
            try {
                Replay replay = primaryFuture.get();

                if(replay.protocolVersion() != MinecraftProtocolVersion.getCurrentVersion()) {
                    secondaryFuture.completeExceptionally(new IllegalStateException("Cannot load " + replay.protocolVersion()
                            + " replay on a " + MinecraftProtocolVersion.getCurrentVersion() + " server."));

                    return;
                }

                this.replay = replay;

                if(replay instanceof PlayingReplay) {
                    this.playback = new PlaybackImpl(this);

                    playback.start();
                }

                secondaryFuture.complete(replay);
            } catch (Throwable throwable) {
                secondaryFuture.completeExceptionally(throwable);
            }
        });

        return secondaryFuture;
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Replay> initReplay0(int replayId, PluginPurpose purpose) {
        Preconditions.checkArgument(purpose == PluginPurpose.PLAY || purpose == PluginPurpose.RECORD,
                "purpose must be one of the following: " + PluginPurpose.PLAY + ", " + PluginPurpose.RECORD
                        + ", " + PluginPurpose.NONE + " (was " + purpose + ')');

        this.purpose = purpose;

        switch (purpose) {
            case PLAY:
                return (CompletableFuture<Replay>) replayStorage.load(replayId);
            case RECORD:
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TickCounter(), 1, 1);

                ProtocolLibrary.getProtocolManager().addPacketListener(new RecordingPacketListener(this));

                return CompletableFuture.completedFuture(new RecordingReplay(replayId));
            default:
                throw new InternalError("Could not handle PluginPurpose: " + purpose); // this should never happen
        }
    }

    @Override
    public Replay currentReplay() {
        return replay;
    }

    @Override
    public PluginPurpose pluginPurpose() {
        return purpose;
    }

    @Override
    public ScheduledExecutorService executorService() {
        return executorService;
    }

    @Override
    public EventIteratorFactory eventIteratorFactory() {
        if(eventIteratorFactory == null) {
            eventIteratorFactory = new EventIteratorFactory(eventRegistry());
        }

        return eventIteratorFactory;
    }

    @Override
    public Class<?> customStorageClass() {
        try {
            return Class.forName(getConfig().getString("customStorageClass"));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public EventRegistry eventRegistry() {
        if(eventRegistry == null) {
            eventRegistry = new EventRegistry();
        }

        return eventRegistry;
    }

    @Override
    public PlaybackImpl playback() {
        return playback;
    }

    public Object handleOf(Object obj) throws ReflectiveOperationException {
        return obj.getClass().getDeclaredMethod("getHandle").invoke(obj);
    }
}
