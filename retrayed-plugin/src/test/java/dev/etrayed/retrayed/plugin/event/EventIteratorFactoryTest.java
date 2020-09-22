package dev.etrayed.retrayed.plugin.event;

import dev.etrayed.retrayed.api.event.TimedEvent;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

/**
 * @author Etrayed
 */
public class EventIteratorFactoryTest {

    @Test
    public void testFactory() throws Exception {
        EventRegistry registry = new EventRegistry();

        registry.registerEvent(0, DummyEvent.class);

        EventIteratorFactory iteratorFactory = new EventIteratorFactory(registry);
        String toStringOutput = iteratorFactory.toString(Collections.singletonList(new TimedEvent(0, new DummyEvent(),
                UUID.randomUUID())).listIterator());

        System.out.println("toString Output: " + toStringOutput);
        System.out.println("Parsed successfully: " + iteratorFactory.fromString(toStringOutput).hasNext());
    }
}
