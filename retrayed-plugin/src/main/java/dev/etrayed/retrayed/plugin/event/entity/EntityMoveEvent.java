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
public class EntityMoveEvent extends AbstractEvent {

    private int entityId;

    private byte movX, movY, movZ;

    private boolean onGround, cachedOnGround;

    public EntityMoveEvent() {
    }

    public EntityMoveEvent(int entityId, byte movX, byte movY, byte movZ, boolean onGround) {
        this.entityId = entityId;
        this.movX = movX;
        this.movY = movY;
        this.movZ = movZ;
        this.onGround = onGround;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            cachedOnGround = entity.isOnGround();

            entity.setOnGround(onGround);
        });

        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, movX).write(1, movY).write(2, movZ);
        container.getBooleans().write(0, onGround);

        stage.sendPacket(container);
    }

    @Override
    public void undo(ReplayStage stage) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, (byte) -movX).write(1, (byte) -movY).write(2, (byte) -movZ);
        container.getBooleans().write(0, cachedOnGround);

        stage.findById(entityId).ifPresent(entity -> entity.setOnGround(cachedOnGround));

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeByte(movX);
        outputStream.writeByte(movY);
        outputStream.writeByte(movZ);
        outputStream.writeBoolean(onGround);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.movX = inputStream.readByte();
        this.movY = inputStream.readByte();
        this.movZ = inputStream.readByte();
        this.onGround = inputStream.readBoolean();
    }
}
