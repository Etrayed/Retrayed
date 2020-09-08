package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.event.BlockingEventIterator;

/**
 * @author Etrayed
 */
public class PlayingReplay extends InternalReplay {

    public PlayingReplay(int id, int protocolVersion) {
        super(id, protocolVersion);
    }

    @Override
    public BlockingEventIterator eventIterator() {
        return null;
    }
}
