package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.util.concurrent.Future;

/**
 * @author Etrayed
 */
public interface ReplayStorage<R extends InternalReplay> {

    Future<R> load(long replayId);

    void save(long replayId);
}
