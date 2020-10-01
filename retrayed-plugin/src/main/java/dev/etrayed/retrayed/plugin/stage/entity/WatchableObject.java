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
        } else if(value instanceof Byte) {
            outputStream.writeByte(10);

            outputStream.writeByte((Byte) value);
        } else if(value instanceof Short) {
            outputStream.writeByte(11);

            outputStream.writeShort((Short) value);
        } else if(value instanceof Character) {
            outputStream.writeByte(12);

            outputStream.writeChar((Character) value);
        } else if(value instanceof Integer) {
            outputStream.writeByte(13);

            outputStream.writeInt((Integer) value);
        } else if(value instanceof Long) {
            outputStream.writeByte(14);

            outputStream.writeLong((Long) value);
        } else if(value instanceof Float) {
            outputStream.writeByte(15);

            outputStream.writeFloat((Float) value);
        } else if(value instanceof Double) {
            outputStream.writeByte(16);

            outputStream.writeDouble((Double) value);
        } else if(value instanceof Boolean) {
            outputStream.writeByte(17);

            outputStream.writeBoolean((Boolean) value);
        } else if(value instanceof String) {
            outputStream.writeByte(18);

            outputStream.writeUTF((String) value);
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
        } else if(type == 10) {
            return inputStream.readByte();
        } else if(type == 11) {
            return inputStream.readShort();
        } else if(type == 12) {
            return inputStream.readChar();
        } else if(type == 13) {
            return inputStream.readInt();
        } else if(type == 14) {
            return inputStream.readLong();
        } else if(type == 15) {
            return inputStream.readFloat();
        } else if(type == 16) {
            return inputStream.readDouble();
        } else if(type == 17) {
            return inputStream.readBoolean();
        } else if(type == 18) {
            return inputStream.readUTF();
        } else {
            return null;
        }
    }

    public static WatchableObject unwrap(WrappedWatchableObject watchableObject) {
        return new WatchableObject(watchableObject.getIndex(), watchableObject.getValue());
    }
}
