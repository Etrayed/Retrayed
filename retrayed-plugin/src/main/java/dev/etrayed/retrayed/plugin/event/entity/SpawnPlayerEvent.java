package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.stage.entity.ReplayPlayer;
import dev.etrayed.retrayed.plugin.stage.entity.WatchableObject;
import dev.etrayed.retrayed.plugin.util.ConversionUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    private int spawnX, spawnY, spawnZ;

    private byte yaw, pitch;

    private int itemInHand;

    private List<WatchableObject> watchableObjects;

    public SpawnPlayerEvent() {
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, int spawnX, int spawnY, int spawnZ, byte yaw, byte pitch) {
        this(entityId, uniqueId, spawnX, spawnY, spawnZ, yaw, pitch, -1, null);
    }

    public SpawnPlayerEvent(int entityId, UUID uniqueId, int spawnX, int spawnY, int spawnZ, byte yaw, byte pitch,
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

    public SpawnPlayerEvent(Player player) {
        this.entityId = player.getEntityId();
        this.uniqueId = player.getUniqueId();
        this.itemInHand = ConversionUtilities.toSafeId(player.getItemInHand());
        this.watchableObjects = WrappedDataWatcher.getEntityWatcher(player).getWatchableObjects().stream()
                .map(WatchableObject::unwrap).collect(Collectors.toList());

        applyLocation(player.getLocation());
    }

    private void applyLocation(Location location) {
        this.spawnX = ConversionUtilities.floorCoordinate(location.getX());
        this.spawnY = ConversionUtilities.floorCoordinate(location.getY());
        this.spawnZ = ConversionUtilities.floorCoordinate(location.getZ());
        this.yaw = ConversionUtilities.correctRotation(location.getYaw());
        this.pitch = ConversionUtilities.correctRotation(location.getPitch());
    }

    @Override
    public void recreate(ReplayStage stage) {
        int entityId = stage.fromLegacyId(this.entityId);
        PacketContainer spawnPlayerPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        spawnPlayerPacket.getIntegers().write(0, entityId);
        spawnPlayerPacket.getUUIDs().write(0, uniqueId);
        spawnPlayerPacket.getIntegers().write(1, spawnX).write(2, spawnY).write(3, spawnZ);
        spawnPlayerPacket.getBytes().write(0, yaw).write(1, pitch);

        if(itemInHand != -1) {
            spawnPlayerPacket.getIntegers().write(4, itemInHand);
        }

        if(watchableObjects != null) {
            spawnPlayerPacket.getDataWatcherModifier().write(0, new WrappedDataWatcher(watchableObjects.stream()
                    .map(WatchableObject::wrap).collect(Collectors.toList())));
        }

        stage.sendPacket(spawnPlayerPacket);
        stage.spawnEntity(new ReplayPlayer(entityId, new Position(spawnX, spawnY, spawnZ, yaw, pitch), uniqueId,
                this, watchableObjects));
    }

    @Override
    public void undo(ReplayStage stage) {
        int entityId = stage.fromLegacyId(this.entityId);
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
        outputStream.writeInt(spawnX);
        outputStream.writeInt(spawnY);
        outputStream.writeInt(spawnZ);
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
        this.spawnX = inputStream.readInt();
        this.spawnY = inputStream.readInt();
        this.spawnZ = inputStream.readInt();
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
