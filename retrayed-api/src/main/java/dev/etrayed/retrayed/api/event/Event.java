package dev.etrayed.retrayed.api.event;

/**
 * @author Etrayed
 */
public interface Event {

    void recreate();

    void undo();
}
