package dev.etrayed.retrayed.api.play;

import java.util.UUID;

/**
 * @author Etrayed
 */
public interface Playback {

    void forward(int ticks);

    void rewind(int ticks);

    void setPaused(boolean paused);

    boolean isPaused();

    void addListener(UUID recordedPlayer, PlaybackPacketListener listener);

    boolean isListening(UUID recordedPlayer, PlaybackPacketListener listener);

    void removeListener(UUID recordedPlayer, PlaybackPacketListener listener);
}
