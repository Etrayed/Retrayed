package dev.etrayed.retrayed.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Etrayed
 */
public interface BlockingEventIterator extends Iterator<Event> {

    @Override
    boolean hasNext();

    @Override
    @NotNull
    Event next();

    void skip(long millis);

    void rollback(long millis);
}
