package dev.etrayed.retrayed.api.player;

import java.util.UUID;

/**
 * @author Etrayed
 */
public interface WrappedPlayer {

    UUID uniqueId();

    <P> P toPlayer();
}
