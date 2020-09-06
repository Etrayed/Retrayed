package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public class MySQLStorage implements ReplayStorage<InternalReplay> {

    private final RetrayedPlugin plugin;

    public MySQLStorage(RetrayedPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<InternalReplay> load(long replayId) {
        return null;
    }

    @Override
    public void save(long replayId) {

    }
}
