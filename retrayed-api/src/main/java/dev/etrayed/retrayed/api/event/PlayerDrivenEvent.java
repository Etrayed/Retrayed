package dev.etrayed.retrayed.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Etrayed
 */
public interface PlayerDrivenEvent extends Event {

    @Override
    void recreate();

    @Override
    void undo();

    @NotNull
    UUID playerId();
}
