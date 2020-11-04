package dev.etrayed.retrayed.api.event;

import java.util.UUID;

/**
 * @author Etrayed
 */
public class TimedEvent {

    private final int tick;

    private final Event event;

    private final UUID receiver;

    public TimedEvent(int tick, Event event, UUID receiver) {
        this.tick = tick;
        this.event = event;
        this.receiver = receiver;
    }

    public int tick() {
        return tick;
    }

    public Event event() {
        return event;
    }

    public UUID receiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "TimedEvent{" +
                "tick=" + tick +
                ", event=" + event +
                ", receiver=" + receiver +
                '}';
    }
}
