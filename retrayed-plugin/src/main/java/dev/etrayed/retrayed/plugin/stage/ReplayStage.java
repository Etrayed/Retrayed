package dev.etrayed.retrayed.plugin.stage;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import dev.etrayed.retrayed.api.play.PlaybackPacketListener;
import dev.etrayed.retrayed.plugin.play.PlaybackImpl;
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

    private final PlaybackImpl playback;

    private final Map<Integer, ReplayEntity> entitiesById;

    private final List<PlaybackPacketListener> listeners;

    private final BiMap<Integer, Integer> swappedEntityIds;

    public ReplayStage(PlaybackImpl playback) {
        this.playback = playback;
        this.entitiesById = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.swappedEntityIds = Maps.synchronizedBiMap(HashBiMap.create());
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

    public int fromLegacyId(int legacyId) {
        return swappedEntityIds.computeIfAbsent(legacyId, unused -> nextEntityId());
    }

    public int toLegacyId(int replayId) {
        return swappedEntityIds.get(replayId);
    }

    private int nextEntityId() {
        return -1;
    }

    public List<PlaybackPacketListener> listeners() {
        return listeners;
    }

    public void sendPacket(PacketContainer packetContainer) {
        listeners.forEach(stageListener -> stageListener.handlePacket(playback, packetContainer));
    }
}
