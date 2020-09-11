package dev.etrayed.retrayed.plugin.stage.entity;

/**
 * @author Etrayed
 */
public abstract class ReplayEntity {

    private final int id;

    public ReplayEntity(int id) {
        this.id = id;
    }

    public final int id() {
        return id;
    }
}
