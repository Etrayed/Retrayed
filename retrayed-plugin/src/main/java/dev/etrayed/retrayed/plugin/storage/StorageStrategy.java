package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.IRetrayedPlugin;

/**
 * @author Etrayed
 */
public enum StorageStrategy {

    MYSQL("MYSQL", MySQLStorage.class);

    private final String key;

    private final Class<? extends ReplayStorage<?, ?>> storageClass;

    StorageStrategy(String key, Class<? extends ReplayStorage<?, ?>> storageClass) {
        this.key = key;
        this.storageClass = storageClass;
    }

    public static StorageStrategy fromString(String key) {
        for (StorageStrategy strategy : values()) {
            if(strategy.key.equalsIgnoreCase(key)) {
                return strategy;
            }
        }

        throw new IllegalArgumentException("invalid storageStrategy: '" + key + '\'');
    }

    public String key() {
        return key;
    }

    public Class<? extends ReplayStorage<?, ?>> storageClass() {
        return storageClass;
    }

    public ReplayStorage<?, ?> createStorage(ReplayStorage.Credentials credentials, IRetrayedPlugin plugin) throws ReflectiveOperationException {
        return storageClass.getDeclaredConstructor(ReplayStorage.Credentials.class, IRetrayedPlugin.class)
                .newInstance(credentials, plugin);
    }
}
