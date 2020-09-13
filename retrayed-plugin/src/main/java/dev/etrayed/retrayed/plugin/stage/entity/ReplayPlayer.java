package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.entity.SpawnPlayerEvent;
import dev.etrayed.retrayed.plugin.stage.Position;

import java.util.UUID;

/**
 * @author Etrayed
 */
public class ReplayPlayer extends AbstractReplayEntity {

    private final UUID uniqueId;

    public ReplayPlayer(int id, Position position, UUID uniqueId, SpawnPlayerEvent spawnEvent) {
        super(id, position, spawnEvent);

        this.uniqueId = uniqueId;
    }

    public UUID uniqueId() {
        return uniqueId;
    }
}
