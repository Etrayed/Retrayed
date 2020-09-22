package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.event.TimedEvent;

import java.util.List;
import java.util.UUID;

/**
 * @author Etrayed
 */
public class PlayingReplay extends InternalReplay {

    private final List<TimedEvent> events;

    public PlayingReplay(int id, int protocolVersion, List<TimedEvent> events, List<UUID> recordedPlayers) {
        super(id, protocolVersion, recordedPlayers);

        this.events = events;
    }

    @Override
    public List<TimedEvent> events() {
        return events;
    }
}
