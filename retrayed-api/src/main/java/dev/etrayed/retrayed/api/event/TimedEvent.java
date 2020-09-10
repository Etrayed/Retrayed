package dev.etrayed.retrayed.api.event;

import java.util.UUID;

/**
 * @author Etrayed
 */
public class TimedEvent {

    private final long time;

    private final Event event;

    private final UUID receiver;

    public TimedEvent(long time, Event event, UUID receiver) {
        this.time = time;
        this.event = event;
        this.receiver = receiver;
    }

    public long time() {
        return time;
    }

    public Event event() {
        return event;
    }

    public UUID receiver() {
        return receiver;
    }
}
