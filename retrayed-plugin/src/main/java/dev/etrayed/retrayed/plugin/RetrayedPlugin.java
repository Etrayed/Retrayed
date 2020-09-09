package dev.etrayed.retrayed.plugin;

import com.google.common.base.Preconditions;
import dev.etrayed.retrayed.api.PluginPurpose;
import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.plugin.event.EventIteratorFactory;
import dev.etrayed.retrayed.plugin.event.EventRegistry;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import dev.etrayed.retrayed.plugin.storage.ReplayStorage;
import dev.etrayed.retrayed.plugin.storage.StorageStrategy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.IOException;
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

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.executorService = Executors.newScheduledThreadPool(0);

        try {
            this.replayStorage = StorageStrategy.fromString(getConfig().getString("storageStrategy"))
                    .createStorage(new ReplayStorage.Credentials(this.getConfig().getConfigurationSection("credentials")), this);
        } catch (ReflectiveOperationException e) {
            getLogger().log(Level.SEVERE, "Could not initialize replayStorage: ", e);

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if(replayStorage != null) {
            try {
                replayStorage.close();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to close replayStorage: ", e);
            }
        }
    }

    @Override
    public Future<Replay> initReplay(int replayId, PluginPurpose purpose) {
        Preconditions.checkArgument(replay != null && purpose != null, "This server has already been initialized.");

        if(purpose == PluginPurpose.NONE) {
            Bukkit.getPluginManager().disablePlugin(this);

            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Replay> future = initReplay0(replayId, purpose);

        future.thenAccept(loadedReplay -> this.replay = loadedReplay); // TODO CHECK IF SERVER VERSION IS THE SAME

        return future;
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
}
