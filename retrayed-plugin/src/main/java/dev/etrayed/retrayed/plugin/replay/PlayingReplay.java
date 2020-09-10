package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.event.TimedEvent;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * @author Etrayed
 */
public class PlayingReplay extends InternalReplay {

    private final ListIterator<TimedEvent> eventIterator;

    public PlayingReplay(int id, int protocolVersion, ListIterator<TimedEvent> eventIterator, List<UUID> recordedPlayers) {
        super(id, protocolVersion, recordedPlayers);

        this.eventIterator = eventIterator;
    }

    @Override
    public ListIterator<TimedEvent> eventIterator() {
        return eventIterator;
    }
}
