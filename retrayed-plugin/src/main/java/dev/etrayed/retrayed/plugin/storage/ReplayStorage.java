package dev.etrayed.retrayed.plugin.storage;

import dev.etrayed.retrayed.plugin.replay.InternalReplay;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import org.bukkit.configuration.ConfigurationSection;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Etrayed
 */
public interface ReplayStorage<R extends InternalReplay, S extends RecordingReplay> extends Closeable {

    CompletableFuture<R> load(int replayId);

    void save(S replay);

    StorageStrategy strategy();

    @Override
    void close() throws IOException;

    class Credentials {

        public final String host;

        public final int port;

        public final String database;

        public final String username;

        public final char[] password;

        public Credentials(ConfigurationSection section) {
            this(section.getString("host"), section.getInt("port"), section.getString("database"),
                    section.getString("username"), section.getString("password").toCharArray());
        }

        Credentials(String host, int port, String database, String username, char[] password) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
        }
    }
}
