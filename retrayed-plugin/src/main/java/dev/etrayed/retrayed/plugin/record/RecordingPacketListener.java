package dev.etrayed.retrayed.plugin.record;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityEquipmentEvent;
import dev.etrayed.retrayed.plugin.event.entity.EntityMetadataEvent;
import dev.etrayed.retrayed.plugin.event.entity.RemoveEntityEvent;
import dev.etrayed.retrayed.plugin.event.entity.SpawnPlayerEvent;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import dev.etrayed.retrayed.plugin.stage.entity.WatchableObject;

import java.util.stream.Collectors;

import static com.comphenix.protocol.PacketType.Play.Server.*;

/**
 * @author Etrayed
 */
public class RecordingPacketListener extends PacketAdapter {

    private final RetrayedPlugin plugin;

    public RecordingPacketListener(RetrayedPlugin plugin) {
        super(new AdapterParameteters().plugin(plugin).gamePhase(GamePhase.PLAYING).serverSide()
                .types(NAMED_ENTITY_SPAWN, ENTITY_DESTROY, ENTITY_EQUIPMENT, ENTITY_METADATA));

        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        PacketContainer container = event.getPacket();

        if(type == NAMED_ENTITY_SPAWN) {
            addEvent(event, new SpawnPlayerEvent(container.getIntegers().read(0), container.getUUIDs().read(0),
                    container.getIntegers().read(1), container.getIntegers().read(2), container.getIntegers().read(3),
                    container.getBytes().read(0), container.getBytes().read(0), container.getIntegers().read(4),
                    container.getWatchableCollectionModifier().read(0).stream().map(WatchableObject::unwrap).collect(Collectors.toList())));
        } else if(type == ENTITY_DESTROY) {
            addEvent(event, new RemoveEntityEvent(container.getIntegerArrays().read(0)));
        } else if(type == ENTITY_EQUIPMENT) {
            addEvent(event, new EntityEquipmentEvent(container.getIntegers().read(0), EntityEquipmentEvent.VersionedEquipmentSlot
                    .fromLegacyIndex(container.getIntegers().read(0)), container.getItemModifier().read(0)));
        } else if(type == ENTITY_METADATA) {
            addEvent(event, new EntityMetadataEvent(container.getIntegers().read(0), container.getWatchableCollectionModifier()
                    .read(0).stream().map(WatchableObject::unwrap).collect(Collectors.toList())));
        }
    }

    private void addEvent(PacketEvent packetEvent, AbstractEvent event) {
        ((RecordingReplay) plugin.currentReplay()).addEvent(TickCounter.currentTick(), event,
                packetEvent.getPlayer().getUniqueId());
    }
}
