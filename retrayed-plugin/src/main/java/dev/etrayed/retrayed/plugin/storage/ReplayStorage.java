package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public interface ReplayStorage<R extends InternalReplay> extends Closeable {

    CompletableFuture<R> load(long replayId);

    void save(long replayId);

    StorageStrategy strategy();

    @Override
    void close() throws IOException;
}
