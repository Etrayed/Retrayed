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
public class EntityHeadRotation extends AbstractEvent {

    private int entityId;

    private byte rotation, cachedRotation;

    public EntityHeadRotation() {
    }

    public EntityHeadRotation(int entityId, byte rotation) {
        this.entityId = entityId;
        this.rotation = rotation;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            cachedRotation = entity.headRotation();

            sendHeadRotation(stage, rotation);

            entity.setHeadRotation(rotation);
        });
    }

    @Override
    public void undo(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            sendHeadRotation(stage, cachedRotation);

            entity.setHeadRotation(cachedRotation);

            cachedRotation = 0;
        });
    }

    private void sendHeadRotation(ReplayStage stage, byte rotation) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);

        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, rotation);

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeByte(rotation);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.rotation = inputStream.readByte();
    }
}
