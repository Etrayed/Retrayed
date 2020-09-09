package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.event.TimedEvent;

import java.util.ListIterator;

/**
 * @author Etrayed
 */
public class PlayingReplay extends InternalReplay {

    private final ListIterator<TimedEvent> eventIterator;

    public PlayingReplay(int id, int protocolVersion, ListIterator<TimedEvent> eventIterator) {
        super(id, protocolVersion);

        this.eventIterator = eventIterator;
    }

    @Override
    public ListIterator<TimedEvent> eventIterator() {
        return eventIterator;
    }
}
