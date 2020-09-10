package dev.etrayed.retrayed.api;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

        public final List<UUID> recordedPlayers;

        public SimplifiedReplay(int id, int protocolVersion, @NotNull String eventData, @NotNull List<UUID> recordedPlayers) {
            this.id = id;
            this.protocolVersion = protocolVersion;
            this.eventData = eventData;
            this.recordedPlayers = Collections.unmodifiableList(recordedPlayers);
        }
    }
}
