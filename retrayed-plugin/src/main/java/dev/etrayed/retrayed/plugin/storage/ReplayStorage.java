package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public interface ReplayStorage<R extends InternalReplay> {

    CompletableFuture<R> load(long replayId);

    void save(long replayId);
}
