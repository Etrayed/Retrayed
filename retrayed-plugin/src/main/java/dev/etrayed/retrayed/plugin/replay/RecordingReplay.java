package dev.etrayed.retrayed.plugin.replay;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import dev.etrayed.retrayed.api.event.TimedEvent;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Etrayed
 */
public class RecordingReplay extends InternalReplay {

    private final List<TimedEvent> timedEvents;

    private long lastEventNs = -1;

    public RecordingReplay(int id) {
        this(id, MinecraftProtocolVersion.getCurrentVersion());
    }

    public RecordingReplay(int id, int protocolVersion) {
        super(id, protocolVersion);

        this.timedEvents = new CopyOnWriteArrayList<>();
    }

    @Override
    public ListIterator<TimedEvent> eventIterator() {
        return Collections.unmodifiableList(timedEvents).listIterator();
    }

    public void addEvent(AbstractEvent event) {
        timedEvents.add(new TimedEvent(lastEventNs == -1 ? 0 : (System.nanoTime() - lastEventNs), event));
    }
}
