package dev.etrayed.retrayed.plugin.replay;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import dev.etrayed.retrayed.api.event.TimedEvent;

import java.util.ListIterator;

/**
 * @author Etrayed
 */
public class RecordingReplay extends InternalReplay {

    public RecordingReplay(int id) {
        this(id, MinecraftProtocolVersion.getCurrentVersion());
    }

    public RecordingReplay(int id, int protocolVersion) {
        super(id, protocolVersion);
    }

    @Override
    public ListIterator<TimedEvent> eventIterator() {
        return null; // TODO: should be dynamically parsed by current recorded data
    }
}
