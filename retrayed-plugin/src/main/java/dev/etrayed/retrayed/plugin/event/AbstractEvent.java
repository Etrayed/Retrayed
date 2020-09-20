package dev.etrayed.retrayed.plugin.event;

import dev.etrayed.retrayed.api.event.Event;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Etrayed
 */
public abstract class AbstractEvent implements Event {

    public abstract void recreate(ReplayStage stage);

    public abstract void undo(ReplayStage stage);

    public abstract void storeIn(ObjectOutputStream outputStream) throws Exception;

    public abstract void takeFrom(ObjectInputStream inputStream) throws Exception;
}
