package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.stage.entity.ReplayPlayer;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Etrayed
 */
public class SpawnPlayerEvent extends AbstractEvent {

    private int entityId;

    private UUID uniqueId;

    private double spawnX, spawnY, spawnZ;

    private byte yaw, pitch;

    private int itemInHand;

    private List<WrappedWatchableObject> watchableObjects;

    public SpawnPlayerEvent() {
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, double spawnX, double spawnY, double spawnZ, byte yaw, byte pitch) {
        this(entityId, uniqueId, spawnX, spawnY, spawnZ, yaw, pitch, -1, null);
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, double spawnX, double spawnY, double spawnZ, byte yaw, byte pitch,
                            int itemInHand, List<WrappedWatchableObject> watchableObjects) {
        this.entityId = entityId;
        this.uniqueId = uniqueId;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.itemInHand = itemInHand;
        this.watchableObjects = watchableObjects;
    }

    @Override
    public void recreate(ReplayStage stage) {
        PacketContainer spawnPlayerPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        spawnPlayerPacket.getIntegers().write(0, entityId);
        spawnPlayerPacket.getUUIDs().write(0, uniqueId);
        spawnPlayerPacket.getDoubles().write(0, spawnX).write(1, spawnY).write(2, spawnZ);
        spawnPlayerPacket.getBytes().write(0, yaw).write(1, pitch);

        if(itemInHand != -1) {
            spawnPlayerPacket.getIntegers().write(1, itemInHand);
        }

        if(watchableObjects != null) {
            spawnPlayerPacket.getWatchableCollectionModifier().write(0, watchableObjects);
        }

        stage.sendPacket(spawnPlayerPacket);
        stage.spawnEntity(new ReplayPlayer(stage.nextEntityId(), new Position(spawnX, spawnY, spawnZ, yaw, pitch), uniqueId));
    }

    @Override
    public void undo(ReplayStage stage) {
        PacketContainer removeEntityPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        removeEntityPacket.getIntegerArrays().write(0, new int[] {entityId});

        stage.sendPacket(removeEntityPacket);
        stage.removeEntity(entityId);
    }

    @Override
    public void storeIn(JsonObject object) {
        object.addProperty("entityId", entityId);
        object.addProperty("uniqueId", uniqueId.toString());
        object.addProperty("spawnX", spawnX);
        object.addProperty("spawnY", spawnY);
        object.addProperty("spawnZ", spawnZ);
        object.addProperty("yaw", yaw);
        object.addProperty("pitch", pitch);
        object.addProperty("itemInHand", itemInHand);
        object.add("watchableObjects", listToArray(watchableObjects, watchableObject -> {
            JsonObject watchableJsonObject = new JsonObject();

            watchableJsonObject.addProperty("index", watchableObject.getIndex());
            watchableJsonObject.addProperty("dirtyState", watchableObject.getDirtyState());

            try {
                watchableJsonObject.add("value", serializeWatchableObjectValue(watchableObject));
            } catch (IOException e) {
                e.printStackTrace();

                watchableJsonObject.add("value", JsonNull.INSTANCE);
            }

            return watchableJsonObject;
        }));
    }

    @Override
    public void takeFrom(JsonObject object) {
        this.entityId = object.get("entityId").getAsInt();
        this.uniqueId = UUID.fromString(object.get("uniqueId").getAsString());
        this.spawnX = object.get("spawnX").getAsDouble();
        this.spawnY = object.get("spawnY").getAsDouble();
        this.spawnZ = object.get("spawnZ").getAsDouble();
        this.yaw = object.get("yaw").getAsByte();
        this.pitch = object.get("pitch").getAsByte();
        this.itemInHand = object.get("itemInHand").getAsInt();
        this.watchableObjects = arrayToList(object.get("watchableObjects"), element -> {
            JsonObject watchableObject = element.getAsJsonObject();

            try {
                WrappedWatchableObject wrappedWatchableObject = new WrappedWatchableObject(watchableObject.get("index").getAsInt(),
                        deserializeWatchableObjectValue(watchableObject.get("value").getAsJsonObject()));

                wrappedWatchableObject.setDirtyState(watchableObject.get("dirtyState").getAsBoolean());

                return wrappedWatchableObject;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();

                return null;
            }
        });
    }
}
