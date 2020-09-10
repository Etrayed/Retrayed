package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.Replay;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Etrayed
 */
public abstract class InternalReplay implements Replay {

    private final int id;

    private final int protocolVersion;

    final List<UUID> recordedPlayers;

    InternalReplay(int id, int protocolVersion, List<UUID> recordedPlayers) {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.recordedPlayers = recordedPlayers;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int protocolVersion() {
        return protocolVersion;
    }

    @Override
    public List<UUID> recordedPlayers() {
        return Collections.unmodifiableList(recordedPlayers);
    }
}
