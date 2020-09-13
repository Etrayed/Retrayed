package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;

/**
 * @author Etrayed
 */
public abstract class AbstractReplayEntity implements ReplayEntity {

    private final int id;

    private Position position;

    private AbstractEvent spawnEvent;

    public AbstractReplayEntity(int id, Position position) {
        this(id, position, null);
    }

    public AbstractReplayEntity(int id, Position position, AbstractEvent spawnEvent) {
        this.id = id;
        this.position = position;
        this.spawnEvent = spawnEvent;
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final Position position() {
        return position;
    }

    @Override
    public final AbstractEvent spawnEvent() {
        return spawnEvent;
    }
}
