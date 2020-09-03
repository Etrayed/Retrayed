package dev.etrayed.retrayed.plugin.replay;

import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import dev.etrayed.retrayed.api.record.BlockingRecordedPacketIterator;

/**
 * @author Etrayed
 */
public class RecordingReplay extends InternalReplay {

    public RecordingReplay(long id) {
        super(id, MinecraftProtocolVersion.getCurrentVersion());
    }

    @Override
    public BlockingRecordedPacketIterator packetIterator() {
        throw new IllegalStateException("cannot iterate through a replay which is currently being recorded");
    }
}
