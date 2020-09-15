package dev.etrayed.retrayed.plugin.event;

import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 * @author Etrayed
 */
public class DummyEvent extends AbstractEvent {

    private int anInt = 33;

    @Override
    public void recreate(ReplayStage stage) {

    }

    @Override
    public void undo(ReplayStage stage) {

    }

    @Override
    public void storeIn(BukkitObjectOutputStream outputStream) {
        outputStream.addProperty("anInt", anInt);
    }

    @Override
    public void takeFrom(BukkitObjectInputStream object) {
        this.anInt = object.get("anInt").getAsInt();
    }

    @Override
    public String toString() {
        return "DummyEvent{" +
                "anInt=" + anInt +
                '}';
    }
}
