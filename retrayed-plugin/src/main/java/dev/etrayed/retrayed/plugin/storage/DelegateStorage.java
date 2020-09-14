package dev.etrayed.retrayed.plugin.storage;

import com.google.common.base.Preconditions;
import dev.etrayed.retrayed.api.CustomReplayStorage;
import dev.etrayed.retrayed.plugin.IRetrayedPlugin;
import dev.etrayed.retrayed.plugin.replay.InternalReplay;
import dev.etrayed.retrayed.plugin.replay.PlayingReplay;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public class DelegateStorage implements ReplayStorage<InternalReplay> {

    private final IRetrayedPlugin plugin;

    private final CustomReplayStorage delegate;

    public DelegateStorage(Credentials credentials, IRetrayedPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.delegate = (CustomReplayStorage) plugin.customStorageClass().getConstructor().newInstance();
    }

    @Override
    public CompletableFuture<InternalReplay> load(int replayId) {
        CompletableFuture<InternalReplay> future = new CompletableFuture<>();

        plugin.executorService().execute(() -> {
            try {
                CustomReplayStorage.SimplifiedReplay replay = delegate.load(replayId).get();

                Preconditions.checkNotNull(replay, "replay cannot be null");

                future.complete(new PlayingReplay(replay.id, replay.protocolVersion, plugin.eventIteratorFactory()
                        .fromString(replay.eventData), replay.recordedPlayers));
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public void save(RecordingReplay replay) {
        try {
            delegate.save(new CustomReplayStorage.SimplifiedReplay(replay.id(), replay.protocolVersion(),
                    plugin.eventIteratorFactory().toString(replay.eventIterator()), replay.recordedPlayers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StorageStrategy strategy() {
        return StorageStrategy.CUSTOM;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
