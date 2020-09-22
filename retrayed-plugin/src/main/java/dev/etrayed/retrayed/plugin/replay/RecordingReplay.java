package dev.etrayed.retrayed.plugin.replay;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import dev.etrayed.retrayed.api.event.TimedEvent;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Etrayed
 */
public class RecordingReplay extends InternalReplay {

    private final List<TimedEvent> timedEvents;

    public RecordingReplay(int id) {
        this(id, MinecraftProtocolVersion.getCurrentVersion());
    }

    public RecordingReplay(int id, int protocolVersion) {
        super(id, protocolVersion, new ArrayList<>());

        this.timedEvents = new CopyOnWriteArrayList<>();
    }

    @Override
    public ListIterator<TimedEvent> eventIterator() {
        return Collections.unmodifiableList(timedEvents).listIterator();
    }

    public void addEvent(int tick, AbstractEvent event, UUID receiver) {
        if(!recordedPlayers.contains(receiver)) {
            recordedPlayers.add(receiver);
        }

        timedEvents.add(new TimedEvent(tick, event, receiver));
    }
}
