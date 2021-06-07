package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Etrayed
 */
public class EntityMoveLookEvent extends AbstractEvent {

    private int entityId;

    private byte movX, movY, movZ, rotYaw, rotPitch;

    private boolean onGround, cachedOnGround;

    public EntityMoveLookEvent() {
    }

    public EntityMoveLookEvent(int entityId, byte movX, byte movY, byte movZ, byte rotYaw, byte rotPitch, boolean onGround) {
        this.entityId = entityId;
        this.movX = movX;
        this.movY = movY;
        this.movZ = movZ;
        this.rotYaw = rotYaw;
        this.rotPitch = rotPitch;
        this.onGround = onGround;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            cachedOnGround = entity.isOnGround();

            entity.setOnGround(onGround);

            sendMoveLookContainer(stage, movX, movY, movZ, rotYaw, rotPitch, onGround);
        });
    }

    @Override
    public void undo(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            entity.setOnGround(cachedOnGround);

            sendMoveLookContainer(stage, (byte) -movX, (byte) -movY, (byte) -movZ, (byte) -rotYaw, (byte) -rotPitch, cachedOnGround);

            cachedOnGround = false;
        });
    }

    private void sendMoveLookContainer(ReplayStage stage, byte movX, byte movY, byte movZ, byte rotYaw, byte rotPitch, boolean onGround) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, movX).write(1, movY).write(2, movZ)
                .write(3, rotYaw).write(4, rotPitch);
        container.getBooleans().write(0, onGround).write(1, true);

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeByte(movX);
        outputStream.writeByte(movY);
        outputStream.writeByte(movZ);
        outputStream.writeByte(rotYaw);
        outputStream.writeByte(rotPitch);
        outputStream.writeBoolean(onGround);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.movX = inputStream.readByte();
        this.movY = inputStream.readByte();
        this.movZ = inputStream.readByte();
        this.rotYaw = inputStream.readByte();
        this.rotPitch = inputStream.readByte();
        this.onGround = inputStream.readBoolean();
    }
}
