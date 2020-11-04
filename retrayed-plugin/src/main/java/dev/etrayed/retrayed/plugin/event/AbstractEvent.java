package dev.etrayed.retrayed.plugin.event;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import dev.etrayed.retrayed.api.event.Event;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Etrayed
 */
public abstract class AbstractEvent implements Event {

    public abstract void recreate(ReplayStage stage);

    public abstract void undo(ReplayStage stage);

    public abstract void storeIn(ObjectOutputStream outputStream) throws Exception;

    public abstract void takeFrom(ObjectInputStream inputStream) throws Exception;

    protected void writeNullableString(ObjectOutputStream outputStream, String str) throws Exception {
        outputStream.writeBoolean(str != null);

        if(str != null) {
            outputStream.writeUTF(str);
        }
    }

    protected void serializeGameProfile(ObjectOutputStream outputStream, WrappedGameProfile profile) throws Exception {
        boolean validUUID = profile.getUUID() != null;

        outputStream.writeBoolean(validUUID);

        if(validUUID) {
            outputStream.writeLong(profile.getUUID().getMostSignificantBits());
            outputStream.writeLong(profile.getUUID().getLeastSignificantBits());
        }

        writeNullableString(outputStream, profile.getName());

        outputStream.writeInt(profile.getProperties().size());

        for (String key : profile.getProperties().keys()) {
            outputStream.writeUTF(key);

            Collection<WrappedSignedProperty> properties = profile.getProperties().get(key);

            outputStream.writeInt(properties.size());

            for (WrappedSignedProperty property : properties) {
                outputStream.writeUTF(property.getName());
                outputStream.writeUTF(property.getValue());

                writeNullableString(outputStream, property.getSignature());
            }
        }
    }

    protected String readNullableString(ObjectInputStream inputStream) throws Exception {
        if(inputStream.readBoolean()) {
            return inputStream.readUTF();
        }

        return null;
    }

    protected WrappedGameProfile deserializeGameProfile(ObjectInputStream inputStream) throws Exception {
        WrappedGameProfile profile = new WrappedGameProfile(inputStream.readBoolean() ? new UUID(inputStream.readLong(),
                inputStream.readLong()) : null, readNullableString(inputStream));

        int properties = inputStream.readInt();

        for (int i = 0; i < properties; i++) {
            String key = inputStream.readUTF();

            int subProperties = inputStream.readInt();

            for (int j = 0; j < subProperties; j++) {
                profile.getProperties().put(key, new WrappedSignedProperty(inputStream.readUTF(), inputStream.readUTF(),
                        readNullableString(inputStream)));
            }
        }

        return profile;
    }
}
