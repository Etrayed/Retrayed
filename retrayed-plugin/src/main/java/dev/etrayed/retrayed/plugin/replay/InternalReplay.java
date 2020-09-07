package dev.etrayed.retrayed.plugin.replay;

import dev.etrayed.retrayed.api.Replay;

/**
 * @author Etrayed
 */
public abstract class InternalReplay implements Replay {

    private final int id;

    private final int protocolVersion;

    public InternalReplay(int id, int protocolVersion) {
        this.id = id;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int protocolVersion() {
        return protocolVersion;
    }
}
