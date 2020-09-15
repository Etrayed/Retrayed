package dev.etrayed.retrayed.plugin.event.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 * @author Etrayed
 */
public class EntityEquipmentEvent extends AbstractEvent {

    private int entityId;

    private VersionedEquipmentSlot equipmentSlot;

    private ItemStack itemStack;

    private ItemStack oldEquipment;

    public EntityEquipmentEvent() {
    }

    public EntityEquipmentEvent(int entityId, VersionedEquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.entityId = entityId;
        this.equipmentSlot = equipmentSlot;
        this.itemStack = itemStack;
    }

    @Override
    public void recreate(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            this.oldEquipment = entity.equipment(equipmentSlot);

            entity.setEquipment(equipmentSlot, itemStack);

            sendEquipmentChange(stage, itemStack);
        });
    }

    @Override
    public void undo(ReplayStage stage) {
        stage.findById(entityId).ifPresent(entity -> {
            entity.setEquipment(equipmentSlot, oldEquipment);

            sendEquipmentChange(stage, oldEquipment);

            this.oldEquipment = null;
        });
    }

    private void sendEquipmentChange(ReplayStage stage, ItemStack itemStack) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        container.getIntegers().write(0, entityId);

        if(container.getIntegers().size() > 1) {
            if(!equipmentSlot.isLegacySupport()) {
                return;
            }

            container.getIntegers().write(1, equipmentSlot.legacyIndex());
        } else {
            container.getItemSlots().write(0, equipmentSlot.asItemSlot());
        }

        container.getItemModifier().write(0, itemStack);

        stage.sendPacket(container);
    }

    @Override
    public void storeIn(BukkitObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(entityId);
        outputStream.writeObject(equipmentSlot);
        outputStream.writeObject(itemStack);
    }

    @Override
    public void takeFrom(BukkitObjectInputStream inputStream) throws Exception {
        this.entityId = inputStream.readInt();
        this.equipmentSlot = (VersionedEquipmentSlot) inputStream.readObject();
        this.itemStack = (ItemStack) inputStream.readObject();
    }

    public enum VersionedEquipmentSlot {

        MAIN_HAND(EnumWrappers.ItemSlot.MAINHAND, 0),
        OFF_HAND(EnumWrappers.ItemSlot.OFFHAND),
        FEET(EnumWrappers.ItemSlot.FEET, 1),
        LEGS(EnumWrappers.ItemSlot.LEGS, 2),
        CHEST(EnumWrappers.ItemSlot.CHEST, 3),
        HEAD(EnumWrappers.ItemSlot.HEAD, 4);

        private final EnumWrappers.ItemSlot itemSlot;

        private final int legacyIndex;

        private boolean legacySupport = true;

        VersionedEquipmentSlot(EnumWrappers.ItemSlot itemSlot) {
            this(itemSlot, -1);

            this.legacySupport = false;
        }

        VersionedEquipmentSlot(EnumWrappers.ItemSlot itemSlot, int legacyIndex) {
            this.itemSlot = itemSlot;
            this.legacyIndex = legacyIndex;
        }

        public EnumWrappers.ItemSlot asItemSlot() {
            return itemSlot;
        }

        public int legacyIndex() {
            return legacyIndex;
        }

        public boolean isLegacySupport() {
            return legacySupport;
        }

        public static VersionedEquipmentSlot fromItemSlot(EnumWrappers.ItemSlot itemSlot) {
            for (VersionedEquipmentSlot slot : values()) {
                if(slot.itemSlot == itemSlot) {
                    return slot;
                }
            }

            return null;
        }

        public static VersionedEquipmentSlot fromLegacyIndex(int legacyIndex) {
            for (VersionedEquipmentSlot slot : values()) {
                if(slot.legacySupport && slot.legacyIndex == legacyIndex) {
                    return slot;
                }
            }

            return null;
        }
    }
}
