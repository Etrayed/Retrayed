package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.api.CustomReplayStorage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public class TestingCustomStorage implements CustomReplayStorage {

    private SimplifiedReplay saved;

    @Override
    public CompletableFuture<SimplifiedReplay> load(int replayId) {
        return CompletableFuture.completedFuture(saved);
    }

    @Override
    public void save(SimplifiedReplay replay) {
        this.saved = replay;
    }

    @Override
    public void close() throws IOException {

    }
}
