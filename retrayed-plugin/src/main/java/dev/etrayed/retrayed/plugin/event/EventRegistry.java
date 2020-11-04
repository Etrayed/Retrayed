package dev.etrayed.retrayed.plugin.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityMetadataEvent;
import dev.etrayed.retrayed.plugin.event.entity.RemoveEntityEvent;
import dev.etrayed.retrayed.plugin.event.entity.SpawnPlayerEvent;
import dev.etrayed.retrayed.plugin.event.other.PlayerInfoEvent;

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

        registerEvent(1, SpawnPlayerEvent.class);
        registerEvent(2, RemoveEntityEvent.class);
        registerEvent(3, EntityEquipmentEvent.class);
        registerEvent(4, EntityMetadataEvent.class);
        registerEvent(5, PlayerInfoEvent.class);
    }

    void registerEvent(int id, Class<? extends AbstractEvent> eventClass) {
        eventsById.put(id, eventClass);
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
