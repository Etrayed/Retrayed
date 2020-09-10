package dev.etrayed.retrayed.plugin.event;

import com.google.gson.JsonObject;

/**
 * @author Etrayed
 */
public class DummyEvent extends AbstractEvent {

    private int anInt = 33;

    @Override
    public void recreate() {

    }

    @Override
    public void undo() {

    }

    @Override
    public void storeIn(JsonObject object) {
        object.addProperty("anInt", anInt);
    }

    @Override
    public void takeFrom(JsonObject object) {
        this.anInt = object.get("anInt").getAsInt();
    }

    @Override
    public String toString() {
        return "DummyEvent{" +
                "anInt=" + anInt +
                '}';
    }
}
