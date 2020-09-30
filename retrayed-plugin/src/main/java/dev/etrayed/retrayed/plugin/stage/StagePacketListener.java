package dev.etrayed.retrayed.plugin.stage;

import com.comphenix.protocol.events.PacketContainer;

/**
 * @author Etrayed
 */
public interface StagePacketListener {

    void handlePacket(ReplayStage stage, PacketContainer container);
}
