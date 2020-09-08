package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.DummyPlugin;
import org.junit.Test;

/**
 * @author Etrayed
 */
public class ReplayStorageTest {

    @Test
    public void testStorages() throws ReflectiveOperationException {
        DummyPlugin dummyPlugin = new DummyPlugin();
        ReplayStorage.Credentials credentials = new ReplayStorage.Credentials("localhost", 3306, "test", "localtest",
                new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});

        for (StorageStrategy strategy : StorageStrategy.values()) {
            ReplayStorage<?, ?> storage = strategy.createStorage(credentials, dummyPlugin);

            // TODO: LOAD/SAVE REPLAYS
        }
    }
}
