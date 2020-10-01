package dev.etrayed.retrayed.api;

import dev.etrayed.retrayed.api.play.Playback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Future;

/**
 * @author Etrayed
 */
public interface RetrayedAPI {

    @NotNull
    Future<Replay> initReplay(int replayId, @NotNull PluginPurpose purpose);

    @Nullable
    Replay currentReplay();

    @Nullable
    PluginPurpose pluginPurpose();

    @Nullable
    Playback playback();
}
