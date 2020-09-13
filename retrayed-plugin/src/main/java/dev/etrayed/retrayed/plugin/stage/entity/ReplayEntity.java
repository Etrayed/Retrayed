package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;

/**
 * @author Etrayed
 */
public interface ReplayEntity {

    int id();

    Position position();

    AbstractEvent spawnEvent();
}
