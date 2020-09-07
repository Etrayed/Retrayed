package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.replay.InternalReplay;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etrayed
 */
public class MySQLStorage implements ReplayStorage<InternalReplay> {

    private static final String CONNECTION_STRING_FORMAT = "jdbc:mysql://%s:%d/%s?autoReconnect=true";
    private static final String TABLE_NAME = "retrayed_storage";

    private static final Logger LOGGER = Logger.getLogger("Retrayed-MYSQL");

    private final Connection connection;

    public MySQLStorage(Credentials credentials) throws SQLException {
        this.connection = makeConnection(credentials);
    }

    private Connection makeConnection(Credentials credentials) throws SQLException {
        Connection connection = null;

        try {
            Properties info = new Properties();

            info.setProperty("username", credentials.username);
            info.put("password", credentials.password);

            connection = DriverManager.getConnection(String.format(CONNECTION_STRING_FORMAT,
                    credentials.host,
                    credentials.port,
                    credentials.database), info);

            LOGGER.info("[MYSQL] Successfully connected.");

            createTable(connection);
        } finally {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        }

        return connection;
    }

    private void createTable(Connection connection) throws SQLException {
        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate("CREATE TABLE IF NOT EXIST " + TABLE_NAME + '(' +
                    "replay_id INT, " +
                    "mc_protocol_id SMALLINT, " +
                    "event_data BLOB, " +
                    "PRIMARY KEY (`replay_id`))");
        }
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
            LOGGER.log(Level.SEVERE, "Failed to close connection: ", e);
        }
    }
}
