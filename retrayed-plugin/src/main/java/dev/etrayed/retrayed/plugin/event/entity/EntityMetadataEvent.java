package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import dev.etrayed.retrayed.plugin.stage.entity.WatchableObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etrayed
 */
public class EntityMetadataEvent extends AbstractEvent {

    private int entityId;

    private List<WatchableObject> watchableObjects;

    private List<WatchableObject> cachedValues;

    public EntityMetadataEvent() {
    }

    public EntityMetadataEvent(int entityId, List<WatchableObject> watchableObjects) {
        this.entityId = entityId;
        this.watchableObjects = watchableObjects;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(stage.fromLegacyId(entityId)).ifPresent(entity -> {
            this.cachedValues = new ArrayList<>();

            watchableObjects.forEach(watchableObject -> cachedValues.add(entity.watchableObject(watchableObject.index())));
        });

        sendMetadata(stage, watchableObjects);
    }

    @Override
    public void undo(ReplayStage stage) {
        if(cachedValues != null) {
            stage.findById(stage.fromLegacyId(entityId)).ifPresent(entity -> {
                for (WatchableObject cachedValue : cachedValues) {
                    entity.setWatchableValue(cachedValue.index(), cachedValue.value());
                }

                sendMetadata(stage, cachedValues);
            });
        }
    }

    private void sendMetadata(ReplayStage stage, Collection<WatchableObject> watchableObjects) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

        container.getIntegers().write(0, stage.fromLegacyId(entityId));
        container.getWatchableCollectionModifier().write(0, watchableObjects.stream().map(WatchableObject::wrap)
                .collect(Collectors.toList()));

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeInt(watchableObjects.size());

        for (WatchableObject watchableObject : watchableObjects) {
            watchableObject.serializeTo(outputStream);
        }
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();

        int watchableObjectCount = inputStream.readInt();

        this.watchableObjects = new ArrayList<>(watchableObjectCount);

        for (int i = 0; i < watchableObjectCount; i++) {
            watchableObjects.add(WatchableObject.deserializeFrom(inputStream));
        }
    }
}
