package dev.etrayed.retrayed.api.record;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Etrayed
 */
public interface BlockingRecordedPacketIterator extends Iterator<RecordedPacket> {

    @Override
    boolean hasNext();

    @Override
    @NotNull
    RecordedPacket next();

    @NotNull
    RecordedPacket peek();
}
