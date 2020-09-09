package dev.etrayed.retrayed.plugin.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Etrayed
 */
public class EventRegistry {

    private static final Logger LOGGER = Logger.getLogger("Retrayed-ER");

    private final BiMap<Integer, Class<? extends AbstractEvent>> eventsById;

    public EventRegistry() {
        this.eventsById = HashBiMap.create();

        registerEvents();
    }

    private void registerEvents() {

    }

    public AbstractEvent newEvent(int id) {
        try {
            return eventsById.get(id).getConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to resolve event with id " + id, e);

            return null;
        }
    }

    public int idByEvent(Class<? extends AbstractEvent> eventClass) {
        return eventsById.inverse().get(eventClass);
    }
}
