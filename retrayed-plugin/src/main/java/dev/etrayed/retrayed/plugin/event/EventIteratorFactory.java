package dev.etrayed.retrayed.plugin.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public ListIterator<TimedEvent> fromString(String json) throws Exception {
        if(parser == null) {
            parser = new JsonParser();
        }

        JsonArray array = parser.parse(json).getAsJsonArray();
        List<TimedEvent> events = new ArrayList<>();

        for (JsonElement element : array) {
            if(!element.isJsonObject()) {
                continue;
            }

            events.add(parseTimedEvent(element.getAsJsonObject()));
        }

        return Collections.unmodifiableList(events).listIterator();
    }

    private TimedEvent parseTimedEvent(JsonObject object) throws Exception {
        AbstractEvent event = registry.newEvent(object.get("id").getAsInt());

        event.takeFrom(object.get("storedData").getAsJsonObject());

        return new TimedEvent(object.get("time").getAsLong(), event, UUID.fromString(object.get("receiver").getAsString()));
    }

    public String toString(ListIterator<TimedEvent> iterator) throws Exception {
        JsonArray array = new JsonArray();

        while (iterator.hasNext()) {
            TimedEvent timedEvent = iterator.next();
            JsonObject object = new JsonObject();
            AbstractEvent abstractEvent = (AbstractEvent) timedEvent.event();

            object.addProperty("time", timedEvent.time());
            object.addProperty("receiver", timedEvent.receiver().toString());
            object.addProperty("id", registry.idByEvent(abstractEvent.getClass()));

            JsonObject storedDataObject = new JsonObject();

            abstractEvent.storeIn(storedDataObject);

            object.add("storedData", storedDataObject);

            array.add(object);
        }

        return array.toString();
    }
}
