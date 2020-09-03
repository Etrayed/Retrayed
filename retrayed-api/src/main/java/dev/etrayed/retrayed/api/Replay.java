package dev.etrayed.retrayed.api;

import dev.etrayed.retrayed.api.record.BlockingRecordedPacketIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Etrayed
 */
public interface Replay {

    long id();

    @NotNull
    BlockingRecordedPacketIterator packetIterator();

    int protocolVersion();
}
