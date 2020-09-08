package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.DummyPlugin;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import org.junit.Test;

/**
 * @author Etrayed
 */
public class ReplayStorageTest {

    private static final int REPLAY_ID = 99;

    @Test
    public void testStorages() throws Exception {
        DummyPlugin dummyPlugin = new DummyPlugin();
        ReplayStorage.Credentials credentials = new ReplayStorage.Credentials("localhost", 3306, "test", "localtest",
                new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
        RecordingReplay toSave = new RecordingReplay(REPLAY_ID, 47);

        for (StorageStrategy strategy : StorageStrategy.values()) {
            ReplayStorage<?> storage = strategy.createStorage(credentials, dummyPlugin);

            storage.save(toSave);
            storage.load(REPLAY_ID).get();
        }
    }
}
