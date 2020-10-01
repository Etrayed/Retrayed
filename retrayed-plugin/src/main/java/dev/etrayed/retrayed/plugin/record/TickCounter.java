package dev.etrayed.retrayed.plugin.record;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Etrayed
 */
public class TickCounter implements Runnable {

    private static AtomicInteger currentTick;

    public TickCounter() {
        currentTick = new AtomicInteger();
    }

    @Override
    public void run() {
        currentTick.incrementAndGet();
    }

    public static int currentTick() {
        return currentTick == null ? -1 : currentTick.get();
    }
}
