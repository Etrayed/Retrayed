package dev.etrayed.retrayed.api;

import dev.etrayed.retrayed.api.event.BlockingEventIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Etrayed
 */
public interface Replay {

    int id();

    @NotNull
    BlockingEventIterator eventIterator();

    int protocolVersion();
}
