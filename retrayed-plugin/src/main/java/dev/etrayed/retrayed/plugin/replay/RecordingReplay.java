package dev.etrayed.retrayed.plugin.replay;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import dev.etrayed.retrayed.api.event.BlockingEventIterator;

/**
 * @author Etrayed
 */
public class RecordingReplay extends InternalReplay {

    public RecordingReplay(int id) {
        super(id, MinecraftProtocolVersion.getCurrentVersion());
    }

    @Override
    public BlockingEventIterator eventIterator() {
        throw new IllegalStateException("cannot iterate through a replay which is currently being recorded");
    }
}
