package dev.etrayed.retrayed.plugin.stage;

import dev.etrayed.retrayed.plugin.stage.entity.ReplayEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Etrayed
 */
public class ReplayStage {

    private final Map<Integer, ReplayEntity> entitiesById;

    public ReplayStage() {
        this.entitiesById = new ConcurrentHashMap<>();
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
}
