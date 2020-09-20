package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Etrayed
 */
public abstract class AbstractReplayEntity implements ReplayEntity {

    private final int id;

    private Position position;

    private AbstractEvent spawnEvent;

    private ItemStack[] equipment;

    private Map<Integer, WatchableObject> watchableObjects;

    public AbstractReplayEntity(int id, Position position, AbstractEvent spawnEvent,
                                Collection<WatchableObject> watchableObjects) {
        this.id = id;
        this.position = position;
        this.spawnEvent = spawnEvent;
        this.watchableObjects = new ConcurrentHashMap<>();

        watchableObjects.forEach(watchableObject -> this.watchableObjects.put(watchableObject.index(), watchableObject));
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final Position position() {
        return position;
    }

    @Override
    public final AbstractEvent spawnEvent() {
        return spawnEvent;
    }

    @Override
    public final void setEquipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot, ItemStack itemStack) {
        if(equipment == null) {
            equipment = new ItemStack[EntityEquipmentEvent.VersionedEquipmentSlot.values().length];
        }

        equipment[equipmentSlot.ordinal()] = itemStack;
    }

    @Override
    public final ItemStack equipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot) {
        return equipment == null ? null : equipment[equipmentSlot.ordinal()];
    }

    @Override
    public final void setWatchableValue(int index, Object value) {
        watchableObjects.computeIfAbsent(index, WatchableObject::new).setValue(value);
    }

    @Override
    public final Object watchableValue(int index) {
        WatchableObject watchableObject = watchableObjects.get(index);

        return watchableObject == null ? null : watchableObject.value();
    }

    @Override
    public final Collection<WatchableObject> watchableObjects() {
        return Collections.unmodifiableCollection(watchableObjects.values());
    }
}
