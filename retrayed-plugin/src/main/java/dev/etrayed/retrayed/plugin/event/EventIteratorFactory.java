package dev.etrayed.retrayed.plugin.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.etrayed.retrayed.api.event.TimedEvent;

import java.util.*;

/**
 * @author Etrayed
 */
public class EventIteratorFactory {

    private final EventRegistry registry;

    private JsonParser parser;

    public EventIteratorFactory(EventRegistry registry) {
        this.registry = registry;
    }

    public ListIterator<TimedEvent> fromString(String json) {
        if(parser == null) {
            parser = new JsonParser();
        }

        JsonArray array = parser.parse(json).getAsJsonArray();
        List<TimedEvent> events = new ArrayList<>();

        array.forEach(element -> {
            if(!element.isJsonObject()) {
                return;
            }

            events.add(parseTimedEvent(element.getAsJsonObject()));
        });

        return Collections.unmodifiableList(events).listIterator();
    }

    private TimedEvent parseTimedEvent(JsonObject object) {
        AbstractEvent event = registry.newEvent(object.get("id").getAsInt());

        event.takeFrom(object.get("storedData").getAsJsonObject());

        return new TimedEvent(object.get("time").getAsLong(), event, UUID.fromString(object.get("receiver").getAsString()));
    }

    public String toString(ListIterator<TimedEvent> iterator) {
        JsonArray array = new JsonArray();

        iterator.forEachRemaining(timedEvent -> {
            JsonObject object = new JsonObject();
            AbstractEvent abstractEvent = (AbstractEvent) timedEvent.event();

            object.addProperty("time", timedEvent.time());
            object.addProperty("receiver", timedEvent.receiver().toString());
            object.addProperty("id", registry.idByEvent(abstractEvent.getClass()));

            JsonObject storedDataObject = new JsonObject();

            abstractEvent.storeIn(storedDataObject);

            object.add("storedData", storedDataObject);

            array.add(object);
        });

        return array.toString();
    }
}
