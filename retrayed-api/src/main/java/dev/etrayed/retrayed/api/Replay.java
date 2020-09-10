package dev.etrayed.retrayed.api;

import dev.etrayed.retrayed.api.event.TimedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * @author Etrayed
 */
public interface Replay {

    int id();

    @NotNull
    @Unmodifiable
    ListIterator<TimedEvent> eventIterator();

    int protocolVersion();

    @NotNull
    @Unmodifiable
    List<UUID> recordedPlayers();
}
