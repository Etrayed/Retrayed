package dev.etrayed.retrayed.plugin.stage;

import com.comphenix.protocol.events.PacketContainer;
import dev.etrayed.retrayed.plugin.stage.entity.ReplayEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Etrayed
 */
public class ReplayStage {

    private final Map<Integer, ReplayEntity> entitiesById;

    private final List<StagePacketListener> listeners;

    public ReplayStage() {
        this.entitiesById = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public Optional<ReplayEntity> findById(int id) {
        return Optional.ofNullable(findByIdUnchecked(id));
    }

    public ReplayEntity findByIdUnchecked(int id) {
        return entitiesById.get(id);
    }

    public void spawnEntity(ReplayEntity entity) {
        entitiesById.put(entity.id(), entity);
    }

    public void removeEntity(int id) {
        entitiesById.remove(id);
    }

    public int nextEntityId() {
        return 0;
    }

    public List<StagePacketListener> listeners() {
        return listeners;
    }

    public void sendPacket(PacketContainer packetContainer) {
        listeners.forEach(stageListener -> stageListener.handlePacket(this, packetContainer));
    }
}
