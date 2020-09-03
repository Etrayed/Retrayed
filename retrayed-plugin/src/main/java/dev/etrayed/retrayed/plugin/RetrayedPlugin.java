package dev.etrayed.retrayed.plugin;

import com.google.common.base.Preconditions;
import dev.etrayed.retrayed.api.PluginPurpose;
import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.api.RetrayedAPI;
import dev.etrayed.retrayed.plugin.replay.InternalReplay;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import dev.etrayed.retrayed.plugin.storage.ReplayStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author Etrayed
 */
@Plugin(name = "Retrayed", version = "DEV")
@Author("Etrayed")
@DependsOn(@Dependency("ProtocolLib"))
public class RetrayedPlugin extends JavaPlugin implements RetrayedAPI {

    private ReplayStorage<? extends InternalReplay> replayStorage;

    private Replay replay;

    private PluginPurpose purpose;

    @Override
    public void onLoad() {
        // TODO: init replay storage
    }

    @Override
    public Future<Replay> initReplay(long replayId, PluginPurpose purpose) {
        Preconditions.checkArgument(replay != null && purpose != null, "This server has already been initialized.");

        if(purpose == PluginPurpose.NONE) {
            Bukkit.getPluginManager().disablePlugin(this);

            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Replay> future = initReplay0(replayId, purpose);

        future.thenAccept(loadedReplay -> this.replay = loadedReplay);

        return future;
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Replay> initReplay0(long replayId, PluginPurpose purpose) {
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
}
