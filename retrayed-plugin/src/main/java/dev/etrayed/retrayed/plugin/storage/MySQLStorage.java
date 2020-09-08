package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.IRetrayedPlugin;
import dev.etrayed.retrayed.plugin.replay.InternalReplay;
import dev.etrayed.retrayed.plugin.replay.PlayingReplay;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;

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

    private final IRetrayedPlugin plugin;

    public MySQLStorage(Credentials credentials, IRetrayedPlugin plugin) throws SQLException {
        this.connection = makeConnection(credentials);
        this.plugin = plugin;
    }

    private Connection makeConnection(Credentials credentials) throws SQLException {
        Properties info = new Properties();

        info.setProperty("username", credentials.username);
        info.put("password", credentials.password);

        Connection connection = DriverManager.getConnection(String.format(CONNECTION_STRING_FORMAT,
                credentials.host,
                credentials.port,
                credentials.database), info);

        LOGGER.info("[MYSQL] Successfully connected.");

        createTable(connection);

        return connection;
    }

    private void createTable(Connection connection) throws SQLException {
        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + '(' +
                    "replay_id INT, " +
                    "mc_protocol_id INT, " +
                    "event_data TEXT, " +
                    "PRIMARY KEY (`replay_id`))");
        }
    }

    @SuppressWarnings("JDBCResourceOpenedButNotSafelyClosed")
    @Override
    public CompletableFuture<InternalReplay> load(int replayId) {
        CompletableFuture<InternalReplay> future = new CompletableFuture<>();

        plugin.executorService().submit(() -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME
                    + " WHERE replay_id=?")) {
                statement.setInt(1, replayId);

                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()) {
                    future.complete(new PlayingReplay(replayId, resultSet.getInt("mc_protocol_id"),
                            plugin.eventIteratorFactory().fromString(resultSet.getString("event_data"))));
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public void save(RecordingReplay replay) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_NAME
                + "(replay_id, mc_protocol_id, event_data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE mc_protocol_id=?, event_data=?")) {
            statement.setInt(1, replay.id());

            statement.setInt(2, replay.protocolVersion());
            statement.setInt(4, replay.protocolVersion());

            String eventData = plugin.eventIteratorFactory().toString(replay.eventIterator());

            statement.setString(3, eventData);
            statement.setString(5, eventData);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
