package dev.etrayed.retrayed.plugin.stage;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.apache.commons.lang.exception.CloneFailedException;

/**
 * @author Etrayed
 */
public final class PlayerInfo implements Cloneable {

    private WrappedGameProfile profile;

    private WrappedChatComponent displayName;

    private EnumWrappers.NativeGameMode gameMode;

    private int latency;

    public PlayerInfo() {
    }

    public PlayerInfo(WrappedGameProfile profile, String displayNameJson, EnumWrappers.NativeGameMode gameMode, int latency) {
        this(profile, displayNameJson == null ? null : WrappedChatComponent.fromJson(displayNameJson), gameMode, latency);
    }

    public PlayerInfo(WrappedGameProfile profile, WrappedChatComponent displayName, EnumWrappers.NativeGameMode gameMode, int latency) {
        this.profile = profile;
        this.displayName = displayName;
        this.gameMode = gameMode;
        this.latency = latency;
    }

    public WrappedGameProfile profile() {
        return profile;
    }

    public void setProfile(WrappedGameProfile profile) {
        this.profile = profile;
    }

    public WrappedChatComponent displayName() {
        return displayName;
    }

    public void setDisplayName(WrappedChatComponent displayName) {
        this.displayName = displayName;
    }

    public EnumWrappers.NativeGameMode gameMode() {
        return gameMode;
    }

    public void setGameMode(EnumWrappers.NativeGameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int latency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public PlayerInfoData toPID() {
        return new PlayerInfoData(profile, latency, gameMode, displayName);
    }

    @Override
    public PlayerInfo clone() {
        try {
            return (PlayerInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneFailedException(e);
        }
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "profile=" + profile +
                ", displayName=" + displayName +
                ", gameMode=" + gameMode +
                ", latency=" + latency +
                '}';
    }

    public static PlayerInfo fromPID(PlayerInfoData infoData) {
        return new PlayerInfo(infoData.getProfile(), infoData.getDisplayName(), infoData.getGameMode(), infoData.getLatency());
    }
}
