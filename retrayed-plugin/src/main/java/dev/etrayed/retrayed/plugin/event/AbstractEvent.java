package dev.etrayed.retrayed.plugin.event;

import com.google.gson.JsonObject;
import dev.etrayed.retrayed.api.event.Event;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

/**
 * @author Etrayed
 */
public abstract class AbstractEvent implements Event {

    public abstract void recreate(ReplayStage stage);

    public abstract void undo(ReplayStage stage);

    public abstract void storeIn(JsonObject object);

    public abstract void takeFrom(JsonObject object);
}
