package dev.etrayed.retrayed.plugin.storage;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Etrayed
 */
public class ReplayStorageTest {

    @Test
    public void testStorages() throws ReflectiveOperationException {
        ReplayStorage.Credentials credentials = new ReplayStorage.Credentials("localhost", 3306, "test", "localtest",
                new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (StorageStrategy strategy : StorageStrategy.values()) {
            ReplayStorage<?, ?> storage = strategy.createStorage(credentials, executorService);

            // TODO: LOAD/SAVE REPLAYS
        }
    }
}
