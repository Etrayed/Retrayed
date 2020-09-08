package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.event.BlockingEventIterator;

/**
 * @author Etrayed
 */
public class PlayingReplay extends InternalReplay {

    private final BlockingEventIterator eventIterator;

    public PlayingReplay(int id, int protocolVersion, BlockingEventIterator eventIterator) {
        super(id, protocolVersion);

        this.eventIterator = eventIterator;
    }

    @Override
    public BlockingEventIterator eventIterator() {
        return eventIterator;
    }
}
