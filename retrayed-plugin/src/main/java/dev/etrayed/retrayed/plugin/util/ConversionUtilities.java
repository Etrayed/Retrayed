package dev.etrayed.retrayed.plugin.util;

import org.bukkit.inventory.ItemStack;

/**
 * @author Etrayed
 */
public final class ConversionUtilities {

    private ConversionUtilities() {
    }

    public static int toSafeId(ItemStack itemStack) {
        return itemStack == null ? 0 : itemStack.getTypeId();
    }

    public static int floorCoordinate(double coordinate) {
        return mathHelperFloor(coordinate * 32.0D);
    }

    public static byte correctRotation(float rotation) {
        return (byte) ((int) (rotation * 256.0F / 360.0F));
    }

    public static int mathHelperFloor(double input) {
        int asInt = (int) input;

        return input < asInt ? asInt - 1 : asInt;
    }
}
