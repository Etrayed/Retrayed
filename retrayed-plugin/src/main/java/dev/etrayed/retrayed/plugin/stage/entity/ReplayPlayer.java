package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.entity.SpawnPlayerEvent;
import dev.etrayed.retrayed.plugin.stage.Position;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Etrayed
 */
public class ReplayPlayer extends AbstractReplayEntity {

    private final UUID uniqueId;

    public ReplayPlayer(int id, Position position, UUID uniqueId, SpawnPlayerEvent spawnEvent,
                        Collection<WatchableObject> watchableObjects) {
        super(id, position, spawnEvent, watchableObjects);

        this.uniqueId = uniqueId;
    }

    public UUID uniqueId() {
        return uniqueId;
    }
}
