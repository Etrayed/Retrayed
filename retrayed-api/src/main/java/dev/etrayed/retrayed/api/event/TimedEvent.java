package dev.etrayed.retrayed.api.event;

/**
 * @author Etrayed
 */
public class TimedEvent {

    private final long time;

    private final Event event;

    public TimedEvent(long time, Event event) {
        this.time = time;
        this.event = event;
    }

    public long time() {
        return time;
    }

    public Event event() {
        return event;
    }
}
