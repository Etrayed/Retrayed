package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Etrayed
 */
public class RemoveEntityEvent extends AbstractEvent {

    private int[] entityIds;

    private Map<Integer, AbstractEvent> cachedSpawnEvents;

    public RemoveEntityEvent() {
    }

    public RemoveEntityEvent(int[] entityIds) {
        this.entityIds = entityIds;
    }

    @Override
    public void recreate(ReplayStage stage) {
        this.cachedSpawnEvents = new HashMap<>();

        for (int entityId : entityIds) {
            stage.findById(entityId).ifPresent(entity -> {
                if(entity.spawnEvent() != null) {
                    cachedSpawnEvents.put(entityId, entity.spawnEvent());
                }
            });
            stage.removeEntity(entityId);
        }

        PacketContainer destroyEntityPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        destroyEntityPacket.getIntegerArrays().write(0, entityIds);

        stage.sendPacket(destroyEntityPacket);
    }

    @Override
    public void undo(ReplayStage stage) {
        if(cachedSpawnEvents != null) {
            cachedSpawnEvents.values().forEach(event -> event.recreate(stage));

            cachedSpawnEvents = null;
        }
    }

    @Override
    public void storeIn(JsonObject object) {
        object.add("entityIds", listToArray(Ints.asList(entityIds), JsonPrimitive::new));
    }

    @Override
    public void takeFrom(JsonObject object) {
        this.entityIds = Ints.toArray(arrayToList(object.get("entityIds"), JsonElement::getAsInt));
    }
}
