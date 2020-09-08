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
        return null; // TODO: should be dynamically parsed by current recorded data
    }
}
