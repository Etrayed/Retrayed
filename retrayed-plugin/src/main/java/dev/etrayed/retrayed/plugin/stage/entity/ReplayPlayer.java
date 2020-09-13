package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.stage.Position;

import java.util.UUID;

/**
 * @author Etrayed
 */
public class ReplayPlayer extends ReplayEntity {

    private final UUID uniqueId;

    public ReplayPlayer(int id, Position position, UUID uniqueId) {
        super(id, position);

        this.uniqueId = uniqueId;
    }

    public UUID uniqueId() {
        return uniqueId;
    }
}
