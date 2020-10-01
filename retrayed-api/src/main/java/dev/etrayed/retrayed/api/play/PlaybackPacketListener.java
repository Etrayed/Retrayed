package dev.etrayed.retrayed.api.play;

import com.comphenix.protocol.events.PacketContainer;

/**
 * @author Etrayed
 */
public interface PlaybackPacketListener {

    void handlePacket(Playback playback, PacketContainer packetContainer);
}
