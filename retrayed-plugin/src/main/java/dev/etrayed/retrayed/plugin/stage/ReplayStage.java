package dev.etrayed.retrayed.plugin.stage;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import dev.etrayed.retrayed.api.play.PlaybackPacketListener;
import dev.etrayed.retrayed.plugin.play.PlaybackImpl;
import dev.etrayed.retrayed.plugin.stage.entity.ReplayEntity;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Etrayed
 */
public class ReplayStage {

    private static final Field ENTITY_COUNT_FIELD;

    static {
        String serverPackage = Bukkit.getServer().getClass().getPackage().getName();

        try {
            ENTITY_COUNT_FIELD = Class.forName("net.minecraft.server." + serverPackage.substring(serverPackage
                    .lastIndexOf('.') + 1) + ".Entity").getDeclaredField("entityCount");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new InternalError(e);
        }
    }

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
        try {
            if(AtomicInteger.class.isAssignableFrom(ENTITY_COUNT_FIELD.getType())) {
                return ((AtomicInteger) ENTITY_COUNT_FIELD.get(null)).incrementAndGet();
            }

            int nextId = ENTITY_COUNT_FIELD.getInt(null) + 1;

            ENTITY_COUNT_FIELD.setInt(null, nextId);

            return nextId;
        } catch (IllegalAccessException e) {
            throw new InternalError(e); // Cannot happen since Entity#entityCount it static
        }
    }

    public List<PlaybackPacketListener> listeners() {
        return listeners;
    }

    public void sendPacket(PacketContainer packetContainer) {
        listeners.forEach(stageListener -> stageListener.handlePacket(playback, packetContainer));
    }
}
