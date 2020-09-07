package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Etrayed
 */
public class MySQLStorage implements ReplayStorage<InternalReplay> {

    private final RetrayedPlugin plugin;

    private final Connection connection;

    public MySQLStorage(RetrayedPlugin plugin) {
        this.plugin = plugin;
        this.connection = makeConnection();
    }

    private Connection makeConnection() {
        return null;
    }

    @Override
    public CompletableFuture<InternalReplay> load(long replayId) {
        return null;
    }

    @Override
    public void save(long replayId) {

    }

    @Override
    public StorageStrategy strategy() {
        return StorageStrategy.MYSQL;
    }

    @Override
    public void close() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to close connection: ", e);
        }
    }
}
