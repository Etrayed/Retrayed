package dev.etrayed.retrayed.plugin.stage.entity;

import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.stage.Position;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * @author Etrayed
 */
public interface ReplayEntity {

    int id();

    Position position();

    AbstractEvent spawnEvent();

    void setEquipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot, ItemStack itemStack);

    ItemStack equipment(EntityEquipmentEvent.VersionedEquipmentSlot equipmentSlot);

    void setWatchableValue(int index, Object value);

    WatchableObject watchableObject(int index);

    Object watchableValue(int index);

    Collection<WatchableObject> watchableObjects();

    boolean isOnGround();

    void setOnGround(boolean onGround);

    void setPosition(Position position);

    byte headRotation();

    void setHeadRotation(byte headRotation);
}
