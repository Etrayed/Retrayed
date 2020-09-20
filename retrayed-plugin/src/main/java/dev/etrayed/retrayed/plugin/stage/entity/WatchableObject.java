package dev.etrayed.retrayed.plugin.stage.entity;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
import com.google.common.base.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Etrayed
 */
public final class WatchableObject {

    private final int index;

    private Object value;

    public WatchableObject(int index) {
        this(index, null);
    }

    public WatchableObject(int index, Object value) {
        this.index = index;
        this.value = value;
    }

    public int index() {
        return index;
    }

    public Object value() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void serializeTo(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(index);

        serializeValueTo(outputStream, value);
    }

    @SuppressWarnings("unchecked")
    private void serializeValueTo(ObjectOutputStream outputStream, Object value) throws IOException {
        if(value instanceof Vector3F) {
            outputStream.writeByte(1);
            outputStream.writeFloat(((Vector3F) value).getX());
            outputStream.writeFloat(((Vector3F) value).getY());
            outputStream.writeFloat(((Vector3F) value).getZ());
        } else if(value instanceof Optional) {
            serializeValueTo(outputStream, ((Optional<?>) value).orNull());
        } else if(value instanceof WrappedChatComponent) {
            outputStream.writeByte(2);
            outputStream.writeUTF(((WrappedChatComponent) value).getJson());
        } else if(value instanceof ItemStack) {
            outputStream.writeByte(3);
            outputStream.writeObject(value);
        } else if(value instanceof WrappedBlockData) {
            outputStream.writeByte(4);
            outputStream.writeObject(((WrappedBlockData) value).getType());
            outputStream.writeInt(((WrappedBlockData) value).getData());
        } else if(value instanceof BlockPosition) {
            outputStream.writeByte(5);
            outputStream.writeInt(((BlockPosition) value).getX());
            outputStream.writeInt(((BlockPosition) value).getY());
            outputStream.writeInt(((BlockPosition) value).getZ());
        } else if(value instanceof EnumWrappers.Direction) {
            outputStream.writeByte(6);
            outputStream.writeObject(value);
        } else if(value instanceof NbtCompound) {
            outputStream.writeByte(7);

            NbtBinarySerializer.DEFAULT.serialize((NbtBase) value, outputStream);
        } else if(value instanceof WrappedChunkCoordinate) {
            outputStream.writeByte(8);

            outputStream.writeInt(((WrappedChunkCoordinate) value).getX());
            outputStream.writeInt(((WrappedChunkCoordinate) value).getY());
            outputStream.writeInt(((WrappedChunkCoordinate) value).getZ());
        } else if(value instanceof ChunkPosition) {
            outputStream.writeByte(9);

            outputStream.writeInt(((ChunkPosition) value).getX());
            outputStream.writeInt(((ChunkPosition) value).getY());
            outputStream.writeInt(((ChunkPosition) value).getZ());
        } else {
            outputStream.writeByte(0);
        }
    }

    public WrappedWatchableObject wrap() {
        return new WrappedWatchableObject(index, value instanceof AbstractWrapper ? ((AbstractWrapper) value).getHandle() : value);
    }

    public static WatchableObject deserializeFrom(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return new WatchableObject(inputStream.readInt(), deserializeValueFrom(inputStream));
    }

    private static Object deserializeValueFrom(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        int type = inputStream.read();

        if(type == 1) {
            return new Vector3F(inputStream.readFloat(), inputStream.readFloat(), inputStream.readFloat());
        } else if(type == 2) {
            return WrappedChatComponent.fromJson(inputStream.readUTF());
        } else if(type == 3) {
            return inputStream.readObject();
        } else if(type == 4) {
            return WrappedBlockData.createData((Material) inputStream.readObject(), inputStream.readInt());
        } else if(type == 5) {
            return new BlockPosition(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
        } else if(type == 6) {
            return inputStream.readObject();
        } else if(type == 7) {
            return NbtBinarySerializer.DEFAULT.deserializeCompound(inputStream);
        } else if(type == 8) {
            return new WrappedChunkCoordinate(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
        } else if(type == 9) {
            return new ChunkPosition(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
        } else {
            return null;
        }
    }
}
