package dev.etrayed.retrayed.plugin.event.other;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.PlayerInfo;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etrayed
 */
public class PlayerInfoEvent extends AbstractEvent {

    private EnumWrappers.PlayerInfoAction action;

    private List<PlayerInfo> infoList;

    private List<Object> cachedValues;

    public PlayerInfoEvent() {
    }

    public PlayerInfoEvent(EnumWrappers.PlayerInfoAction action, List<PlayerInfo> infoList) {
        this.action = action;
        this.infoList = infoList;
    }

    @Override
    public void recreate(ReplayStage stage) {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);

        container.getPlayerInfoAction().write(0, action);
        container.getPlayerInfoDataLists().write(0, infoList.stream().map(PlayerInfo::toPID).collect(Collectors.toList()));

        stage.sendPacket(container);

        cachedValues = new ArrayList<>();

        for (PlayerInfo playerInfo : infoList) {
            if(action == EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME) {
                cachedValues.add(playerInfo.displayName());
            } else if(action == EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE) {
                cachedValues.add(playerInfo.gameMode());
            } else if(action == EnumWrappers.PlayerInfoAction.UPDATE_LATENCY) {
                cachedValues.add(playerInfo.latency());
            }
        }
    }

    @Override
    public void undo(ReplayStage stage) {
        List<PlayerInfo> toSend = new ArrayList<>();

        for (int i = 0; i < infoList.size(); i++) {
            PlayerInfo playerInfo = infoList.get(i).clone();
            Object cachedValue = cachedValues.size() > i ? cachedValues.get(i) : null;

            if(action == EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME) {
                playerInfo.setDisplayName((WrappedChatComponent) cachedValue);
            } else if(action == EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE) {
                playerInfo.setGameMode((EnumWrappers.NativeGameMode) cachedValue);
            } else if(action == EnumWrappers.PlayerInfoAction.UPDATE_LATENCY) {
                playerInfo.setLatency((Integer) cachedValue);
            }

            toSend.add(playerInfo);
        }

        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);

        container.getPlayerInfoAction().write(0, revertAction(action));
        container.getPlayerInfoDataLists().write(0, toSend.stream().map(PlayerInfo::toPID).collect(Collectors.toList()));

        stage.sendPacket(container);
    }

    private EnumWrappers.PlayerInfoAction revertAction(EnumWrappers.PlayerInfoAction action) {
        switch (action) {
            case ADD_PLAYER:
                return EnumWrappers.PlayerInfoAction.REMOVE_PLAYER;
            case REMOVE_PLAYER:
                return EnumWrappers.PlayerInfoAction.ADD_PLAYER;
            case UPDATE_GAME_MODE:
            case UPDATE_LATENCY:
            case UPDATE_DISPLAY_NAME:
            default:
                return action;
        }
    }

    @Override
    public void storeIn(ObjectOutputStream outputStream) throws Exception {
        outputStream.writeByte(action.ordinal());
        outputStream.writeInt(infoList.size());

        for (PlayerInfo playerInfo : infoList) {
            serializeGameProfile(outputStream, playerInfo.profile());
            writeNullableString(outputStream, playerInfo.displayName() == null ? null : playerInfo.displayName().getJson());

            outputStream.writeInt(playerInfo.gameMode().ordinal());
            outputStream.writeInt(playerInfo.latency());
        }
    }

    @Override
    public void takeFrom(ObjectInputStream inputStream) throws Exception {
        this.action = EnumWrappers.PlayerInfoAction.values()[inputStream.readByte()];

        int size = inputStream.readInt();

        this.infoList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            infoList.add(new PlayerInfo(deserializeGameProfile(inputStream), readNullableString(inputStream),
                    EnumWrappers.NativeGameMode.values()[inputStream.readInt()], inputStream.readInt()));
        }
    }
}
