package dev.etrayed.retrayed.api;

import dev.etrayed.retrayed.api.event.TimedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ListIterator;

/**
 * @author Etrayed
 */
public interface Replay {

    int id();

    @NotNull
    ListIterator<TimedEvent> eventIterator();

    int protocolVersion();
}
