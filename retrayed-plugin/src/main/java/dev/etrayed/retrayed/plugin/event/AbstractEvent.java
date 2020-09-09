package dev.etrayed.retrayed.plugin.event;

import com.google.gson.JsonObject;
import dev.etrayed.retrayed.api.event.Event;

/**
 * @author Etrayed
 */
public abstract class AbstractEvent implements Event {

    @Override
    public abstract void recreate();

    @Override
    public abstract void undo();

    abstract void storeIn(JsonObject object);

    abstract void takeFrom(JsonObject object);
}
