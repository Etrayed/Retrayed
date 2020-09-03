package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.Replay;

/**
 * @author Etrayed
 */
public abstract class InternalReplay implements Replay {

    private final long id;

    private final int protocolVersion;

    public InternalReplay(long id, int protocolVersion) {
        this.id = id;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public int protocolVersion() {
        return protocolVersion;
    }
}
