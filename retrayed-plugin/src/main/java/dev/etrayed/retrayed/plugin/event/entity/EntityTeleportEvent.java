package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.util.ConversionUtilities;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Etrayed
 */
public class EntityTeleportEvent extends AbstractEvent {

    private int entityId, x, y, z;

    private byte yaw, pitch;

    private boolean onGround;

    /* Cached stuff */

    private Position cachedPos;

    private boolean cachedOnGround;

    public EntityTeleportEvent() {
    }

    public EntityTeleportEvent(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this(entityId, ConversionUtilities.floorCoordinate(x), ConversionUtilities.floorCoordinate(y),
                ConversionUtilities.floorCoordinate(z), ConversionUtilities.correctRotation(yaw),
                ConversionUtilities.correctRotation(pitch), onGround);
    }

    public EntityTeleportEvent(int entityId, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            cachedPos = entity.position();
            cachedOnGround = entity.isOnGround();

            entity.setPosition(new Position(x, y, z, yaw, pitch));
            entity.setOnGround(onGround);

            sendEntityTeleport(stage, x, y, z, yaw, pitch, onGround);
        });
    }

    @Override
    public void undo(ReplayStage stage) {
        if(cachedPos == null) {
            return;
        }

        stage.findById(entityId).ifPresent(entity -> {
            entity.setPosition(cachedPos);
            entity.setOnGround(cachedOnGround);

            sendEntityTeleport(stage, cachedPos.xInt(), cachedPos.yInt(), cachedPos.zInt(), cachedPos.yaw(),
                    cachedPos.pitch(), cachedOnGround);

            cachedPos = null;
            cachedOnGround = false;
        });
    }

    private void sendEntityTeleport(ReplayStage stage, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        container.getIntegers().write(0, entityId).write(1, x).write(2, y).write(3, z);
        container.getBytes().write(0, yaw).write(1, pitch);
        container.getBooleans().write(0, onGround);

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeInt(x);
        outputStream.writeInt(y);
        outputStream.writeInt(z);
        outputStream.writeByte(yaw);
        outputStream.writeByte(pitch);
        outputStream.writeBoolean(onGround);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.x = inputStream.readInt();
        this.y = inputStream.readInt();
        this.z = inputStream.readInt();
        this.yaw = inputStream.readByte();
        this.pitch = inputStream.readByte();
        this.onGround = inputStream.readBoolean();
    }
}
