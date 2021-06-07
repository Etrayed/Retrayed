package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.util.ConversionUtilities;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Etrayed
 */
public class EntityLookEvent extends AbstractEvent {

    private int entityId;

    private byte yaw, pitch;

    private boolean onGround, cachedOnGround;

    public EntityLookEvent() {
    }

    public EntityLookEvent(int entityId, byte relYaw, byte relPitch, boolean onGround) {
        this.entityId = entityId;
        this.yaw = relYaw;
        this.pitch = relPitch;
        this.onGround = onGround;
    }

    public EntityLookEvent(int entityId, float yaw, float pitch, boolean onGround) {
        this(entityId, ConversionUtilities.correctRotation(yaw), ConversionUtilities.correctRotation(pitch), onGround);
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            cachedOnGround = entity.isOnGround();

            entity.setOnGround(onGround);
        });

        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, yaw).write(1, pitch);
        container.getBooleans().write(0, onGround).write(1, true);

        stage.sendPacket(container);
    }

    @Override
    public void undo(ReplayStage stage) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, (byte) -yaw).write(1, (byte) -pitch);
        container.getBooleans().write(0, cachedOnGround).write(1, true);

        stage.findById(entityId).ifPresent(entity -> entity.setOnGround(cachedOnGround));

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeByte(yaw);
        outputStream.writeByte(pitch);
        outputStream.writeBoolean(onGround);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.yaw = inputStream.readByte();
        this.pitch = inputStream.readByte();
        this.onGround = inputStream.readBoolean();
    }
}
