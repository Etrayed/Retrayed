package dev.etrayed.retrayed.plugin.event;

import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeInt(anInt);
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.anInt = inputStream.readInt();
    }

    @Override
    public String toString() {
        return "DummyEvent{" +
                "anInt=" + anInt +
                '}';
    }
}
