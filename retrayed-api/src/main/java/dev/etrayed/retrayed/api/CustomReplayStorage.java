package dev.etrayed.retrayed.api;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public interface CustomReplayStorage extends Closeable {

    @NotNull
    CompletableFuture<SimplifiedReplay> load(int replayId);

    void save(@NotNull SimplifiedReplay replay);

    @Override
    void close() throws IOException;

    final class SimplifiedReplay {

        public final int id, protocolVersion;

        public final String eventData;

        public SimplifiedReplay(int id, int protocolVersion, @NotNull String eventData) {
            this.id = id;
            this.protocolVersion = protocolVersion;
            this.eventData = eventData;
        }
    }
}
