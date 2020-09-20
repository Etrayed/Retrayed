package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.stage.entity.ReplayPlayer;
import dev.etrayed.retrayed.plugin.stage.entity.WatchableObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Etrayed
 */
public class SpawnPlayerEvent extends AbstractEvent {

    private int entityId;

    private UUID uniqueId;

    private double spawnX, spawnY, spawnZ;

    private byte yaw, pitch;

    private int itemInHand;

    private List<WatchableObject> watchableObjects;

    public SpawnPlayerEvent() {
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, double spawnX, double spawnY, double spawnZ, byte yaw, byte pitch) {
        this(entityId, uniqueId, spawnX, spawnY, spawnZ, yaw, pitch, -1, null);
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, double spawnX, double spawnY, double spawnZ, byte yaw, byte pitch,
                            int itemInHand, List<WatchableObject> watchableObjects) {
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
            spawnPlayerPacket.getWatchableCollectionModifier().write(0, watchableObjects.stream()
                    .map(WatchableObject::wrap).collect(Collectors.toList()));
        }

        stage.sendPacket(spawnPlayerPacket);
        stage.spawnEntity(new ReplayPlayer(stage.nextEntityId(), new Position(spawnX, spawnY, spawnZ, yaw, pitch), uniqueId,
                this, watchableObjects));
    }

    @Override
    public void undo(ReplayStage stage) {
        PacketContainer removeEntityPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        removeEntityPacket.getIntegerArrays().write(0, new int[] {entityId});

        stage.sendPacket(removeEntityPacket);
        stage.removeEntity(entityId);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeLong(uniqueId.getMostSignificantBits());
        outputStream.writeLong(uniqueId.getLeastSignificantBits());
        outputStream.writeDouble(spawnX);
        outputStream.writeDouble(spawnY);
        outputStream.writeDouble(spawnZ);
        outputStream.writeByte(yaw);
        outputStream.writeByte(pitch);
        outputStream.writeInt(itemInHand);
        outputStream.writeInt(watchableObjects.size());

        for (WatchableObject watchableObject : watchableObjects) {
            watchableObject.serializeTo(outputStream);
        }
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.uniqueId = new UUID(inputStream.readLong(), inputStream.readLong());
        this.spawnX = inputStream.readDouble();
        this.spawnY = inputStream.readDouble();
        this.spawnZ = inputStream.readDouble();
        this.yaw = inputStream.readByte();
        this.pitch = inputStream.readByte();
        this.itemInHand = inputStream.readInt();

        int watchableObjectsSize = inputStream.readInt();

        this.watchableObjects = new ArrayList<>(watchableObjectsSize);

        for (int i = 0; i < watchableObjectsSize; i++) {
            watchableObjects.add(WatchableObject.deserializeFrom(inputStream));
        }
    }
}
