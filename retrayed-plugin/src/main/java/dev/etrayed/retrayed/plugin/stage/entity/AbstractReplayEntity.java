package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import org.bukkit.inventory.ItemStack;

/**
 * @author Etrayed
 */
public abstract class AbstractReplayEntity implements ReplayEntity {

    private final int id;

    private Position position;

    private AbstractEvent spawnEvent;

    private ItemStack[] equipment;

    public AbstractReplayEntity(int id, Position position) {
        this(id, position, null);
    }

    public AbstractReplayEntity(int id, Position position, AbstractEvent spawnEvent) {
        this.id = id;
        this.position = position;
        this.spawnEvent = spawnEvent;
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
    public void setEquipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot, ItemStack itemStack) {
        if(equipment == null) {
            equipment = new ItemStack[EntityEquipmentEvent.VersionedEquipmentSlot.values().length];
        }

        equipment[equipmentSlot.ordinal()] = itemStack;
    }

    @Override
    public ItemStack equipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot) {
        return equipment == null ? null : equipment[equipmentSlot.ordinal()];
    }
}
