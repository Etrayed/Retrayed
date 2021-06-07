package dev.etrayed.retrayed.plugin.record;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.event.entity.*;
import dev.etrayed.retrayed.plugin.event.other.PlayerInfoEvent;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import dev.etrayed.retrayed.plugin.stage.PlayerInfo;
import dev.etrayed.retrayed.plugin.stage.entity.WatchableObject;

import java.util.stream.Collectors;

import static com.comphenix.protocol.PacketType.Play.*;

/**
 * @author Etrayed
 */
public class RecordingPacketListener extends PacketAdapter {

    private final RetrayedPlugin plugin;

    public RecordingPacketListener(RetrayedPlugin plugin) {
        super(new AdapterParameteters().plugin(plugin).listenerPriority(ListenerPriority.HIGHEST).connectionSide(ConnectionSide.BOTH)
                .types(Server.NAMED_ENTITY_SPAWN, Server.ENTITY_DESTROY, Server.ENTITY_EQUIPMENT, Server.ENTITY_METADATA,
                        Server.PLAYER_INFO, Server.REL_ENTITY_MOVE, Server.ENTITY, Server.ENTITY_LOOK, Server.REL_ENTITY_MOVE_LOOK,
                        Client.FLYING, Client.POSITION, Client.LOOK, Client.POSITION_LOOK));

        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        PacketContainer container = event.getPacket();

        if(type == Server.NAMED_ENTITY_SPAWN) {
            addEvent(event, new SpawnPlayerEvent(container.getIntegers().read(0), container.getUUIDs().read(0),
                    container.getIntegers().read(1), container.getIntegers().read(2), container.getIntegers().read(3),
                    container.getBytes().read(0), container.getBytes().read(0), container.getIntegers().read(4),
                    container.getWatchableCollectionModifier().read(0).stream().map(WatchableObject::unwrap).collect(Collectors.toList())));
        } else if(type == Server.ENTITY_DESTROY) {
            addEvent(event, new RemoveEntityEvent(container.getIntegerArrays().read(0)));
        } else if(type == Server.ENTITY_EQUIPMENT) {
            addEvent(event, new EntityEquipmentEvent(container.getIntegers().read(0), EntityEquipmentEvent.VersionedEquipmentSlot
                    .fromLegacyIndex(container.getIntegers().read(0)), container.getItemModifier().read(0)));
        } else if(type == Server.ENTITY_METADATA) {
            addEvent(event, new EntityMetadataEvent(container.getIntegers().read(0), container.getWatchableCollectionModifier()
                    .read(0).stream().map(WatchableObject::unwrap).collect(Collectors.toList())));
        } else if(type == Server.PLAYER_INFO) {
            addEvent(event, new PlayerInfoEvent(container.getPlayerInfoAction().read(0),
                    container.getPlayerInfoDataLists().read(0).stream().map(PlayerInfo::fromPID).collect(Collectors.toList())));
        } else if(type == Server.REL_ENTITY_MOVE) {
            addEvent(event, new EntityMoveEvent(container.getIntegers().read(0), container.getBytes().read(0),
                    container.getBytes().read(1), container.getBytes().read(2), container.getBooleans().read(0)));
        } else if(type == Server.ENTITY_LOOK) {
            addEvent(event, new EntityLookEvent(container.getIntegers().read(0), container.getBytes().read(3),
                    container.getBytes().read(4), container.getBooleans().read(0)));
        } else if(type == Server.REL_ENTITY_MOVE_LOOK || type == Server.ENTITY) {
            addEvent(event, new EntityMoveLookEvent(container.getIntegers().read(0), container.getBytes().read(0),
                    container.getBytes().read(1), container.getBytes().read(2), container.getBytes().read(3),
                    container.getBytes().read(4), container.getBooleans().read(0)));
        } else if(type == Server.ENTITY_TELEPORT) {
            addEvent(event, new EntityTeleportEvent(container.getIntegers().read(0), container.getIntegers().read(0),
                    container.getIntegers().read(1), container.getIntegers().read(2), container.getBytes().read(0),
                    container.getBytes().read(1), container.getBooleans().read(0)));
        } else if(type == Server.ENTITY_HEAD_ROTATION) {
            addEvent(event, new EntityHeadRotation(container.getIntegers().read(0), container.getBytes().read(0)));
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType type = event.getPacketType();
        PacketContainer container = event.getPacket();

        if(type == Client.POSITION_LOOK || type == Client.FLYING) {
            addEvent(event, new EntityTeleportEvent(event.getPlayer().getEntityId(), container.getDoubles().read(0),
                    container.getDoubles().read(1), container.getDoubles().read(2), container.getFloat().read(0),
                    container.getFloat().read(1), container.getBooleans().read(0)));
        } else if(type == Client.LOOK) {
            addEvent(event, new EntityLookEvent(event.getPlayer().getEntityId(), container.getFloat().read(0),
                    container.getFloat().read(1), container.getBooleans().read(0)));
        }
    }

    private void addEvent(PacketEvent packetEvent, AbstractEvent event) {
        ((RecordingReplay) plugin.currentReplay()).addEvent(TickCounter.currentTick(), event,
                packetEvent.getPlayer().getUniqueId());
    }
}
