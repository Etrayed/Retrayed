package dev.etrayed.retrayed.plugin.event;

import com.google.common.io.BaseEncoding;
import dev.etrayed.retrayed.api.event.TimedEvent;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Etrayed
 */
public class EventIteratorFactory {

    private final EventRegistry registry;

    public EventIteratorFactory(EventRegistry registry) {
        this.registry = registry;
    }

    public ListIterator<TimedEvent> fromString(String encoded) throws Exception {
        List<TimedEvent> events = new ArrayList<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(BaseEncoding.base64().decode(encoded));

        try (BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream)) {
            AbstractEvent event = registry.newEvent(bukkitInputStream.readInt());
            TimedEvent timedEvent = new TimedEvent(bukkitInputStream.readLong(), event, new UUID(bukkitInputStream.readLong(),
                    bukkitInputStream.readLong()));

            event.takeFrom(bukkitInputStream);

            events.add(timedEvent);
        }

        return Collections.unmodifiableList(events).listIterator();
    }

    public String toString(ListIterator<TimedEvent> iterator) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream)) {
            while (iterator.hasNext()) {
                TimedEvent timedEvent = iterator.next();
                AbstractEvent abstractEvent = (AbstractEvent) timedEvent.event();

                bukkitOutputStream.writeInt(registry.idByEvent(abstractEvent.getClass()));
                bukkitOutputStream.writeLong(timedEvent.time());
                bukkitOutputStream.writeLong(timedEvent.receiver().getMostSignificantBits());
                bukkitOutputStream.writeLong(timedEvent.receiver().getLeastSignificantBits());

                abstractEvent.storeIn(bukkitOutputStream);
            }
        }

        return BaseEncoding.base64().encode(outputStream.toByteArray());
    }
}
