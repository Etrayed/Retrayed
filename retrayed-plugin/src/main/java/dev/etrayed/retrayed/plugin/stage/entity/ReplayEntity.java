package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.stage.Position;

/**
 * @author Etrayed
 */
public abstract class ReplayEntity {

    private final int id;

    private Position position;

    public ReplayEntity(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public final int id() {
        return id;
    }

    public final Position position() {
        return position;
    }
}
