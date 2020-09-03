package dev.etrayed.retrayed.api.record;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Etrayed
 */
public interface RecordedPacket {

    int packetId();

    byte[] data();

    @Nullable
    UUID sender();
}
