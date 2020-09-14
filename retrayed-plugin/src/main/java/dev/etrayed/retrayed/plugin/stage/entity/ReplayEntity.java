package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import org.bukkit.inventory.ItemStack;

/**
 * @author Etrayed
 */
public interface ReplayEntity {

    int id();

    Position position();

    AbstractEvent spawnEvent();

    void setEquipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot, ItemStack itemStack);

    ItemStack equipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot);
}
