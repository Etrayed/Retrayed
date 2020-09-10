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

    public abstract void storeIn(JsonObject object);

    public abstract void takeFrom(JsonObject object);
}
