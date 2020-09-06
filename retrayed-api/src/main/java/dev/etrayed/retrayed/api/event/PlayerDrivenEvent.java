package dev.etrayed.retrayed.api.event;

import dev.etrayed.retrayed.api.player.WrappedPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Etrayed
 */
public interface PlayerDrivenEvent extends Event {

    @Override
    void recreate();

    @Override
    void undo();

    @NotNull
    WrappedPlayer player();
}
