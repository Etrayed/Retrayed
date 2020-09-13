package dev.etrayed.retrayed.plugin.event;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.google.gson.*;
import dev.etrayed.retrayed.api.event.Event;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Etrayed
 */
public abstract class AbstractEvent implements Event {

    public abstract void recreate(ReplayStage stage);

    public abstract void undo(ReplayStage stage);

    public abstract void storeIn(JsonObject object) throws Exception;

    protected <T> JsonElement listToArray(Iterable<T> iterable, Function<T, JsonElement> function) {
        if(iterable == null) {
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();

        iterable.forEach(t -> array.add(function.apply(t)));

        return array;
    }

    protected <T> JsonElement toJsonArray(Function<T, JsonElement> function, T[] array) {
        if(array == null) {
            return JsonNull.INSTANCE;
        }

        JsonArray jsonArray = new JsonArray();

        for (T t : array) {
            jsonArray.add(function.apply(t));
        }

        return jsonArray;
    }

    protected JsonObject serializeWatchableObjectValue(WrappedWatchableObject watchableObject) throws IOException {
        return serializeWatchableObjectValue0(watchableObject.getValue());
    }

    @SuppressWarnings("unchecked")
    private JsonObject serializeWatchableObjectValue0(Object value) throws IOException {
        JsonObject object = new JsonObject();
        int type;

        if(value instanceof Vector3F) {
            type = 1;

            object.addProperty("x", ((Vector3F) value).getX());
            object.addProperty("y", ((Vector3F) value).getY());
            object.addProperty("z", ((Vector3F) value).getZ());
        } else if(value instanceof Optional) {
            return serializeWatchableObjectValue0(((Optional<?>) value).orNull());
        } else if(value instanceof WrappedChatComponent) {
            type = 2;

            object.addProperty("json", ((WrappedChatComponent) value).getJson());
        } else if(value instanceof ItemStack) {
            type = 3;

            object.addProperty("encoded", configurationSerializableToString((ConfigurationSerializable) value));
        } else if(value instanceof WrappedBlockData) {
            type = 4;

            object.addProperty("type", ((WrappedBlockData) value).getType().name());
            object.addProperty("data", ((WrappedBlockData) value).getData());
        } else if(value instanceof BlockPosition) {
            type = 5;

            object.addProperty("x", ((BlockPosition) value).getX());
            object.addProperty("y", ((BlockPosition) value).getY());
            object.addProperty("z", ((BlockPosition) value).getZ());
        } else if(value instanceof EnumWrappers.Direction) {
            type = 6;

            object.addProperty("direction", ((Enum) value).name());
        } else if(value instanceof NbtCompound) {
            type = 7;

            object.addProperty("encoded", NbtTextSerializer.DEFAULT.serialize((NbtBase) value));
        } else if(value instanceof WrappedChunkCoordinate) {
            type = 8;

            object.addProperty("x", ((WrappedChunkCoordinate) value).getX());
            object.addProperty("y", ((WrappedChunkCoordinate) value).getY());
            object.addProperty("z", ((WrappedChunkCoordinate) value).getZ());
        } else if(value instanceof ChunkPosition) {
            type = 9;

            object.addProperty("x", ((ChunkPosition) value).getX());
            object.addProperty("y", ((ChunkPosition) value).getY());
            object.addProperty("z", ((ChunkPosition) value).getZ());
        } else {
            type = 0;
        }

        object.addProperty("__type__", type);

        return object;
    }

    protected String configurationSerializableToString(ConfigurationSerializable serializable) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream)) {
            bukkitOutputStream.writeObject(serializable);

            return BaseEncoding.base64().encode(outputStream.toByteArray());
        }
    }

    public abstract void takeFrom(JsonObject object) throws Exception;

    protected <T> List<T> arrayToList(JsonElement element, Function<JsonElement, T> function) {
        if(!element.isJsonArray()) {
            return null;
        }

        List<T> list = new ArrayList<>();

        element.getAsJsonArray().forEach(jsonElement -> list.add(function.apply(jsonElement)));

        return list;
    }

    protected <T> T[] fromJsonArray(JsonElement element, Function<JsonElement, T> function, Function<Integer, T[]> arrayCreator) {
        if(!element.isJsonArray()) {
            return null;
        }

        JsonArray jsonArray = element.getAsJsonArray();
        T[] array = arrayCreator.apply(jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            array[i] = function.apply(jsonArray.get(i));
        }

        return array;
    }

    protected Object deserializeWatchableObjectValue(JsonObject object) throws IOException, ClassNotFoundException {
        int type = object.get("__type__").getAsInt();

        if(type == 1) {
            return new Vector3F(object.get("x").getAsFloat(), object.get("y").getAsFloat(), object.get("z").getAsFloat());
        } else if(type == 2) {
            return WrappedChatComponent.fromJson(object.get("json").getAsString());
        } else if(type == 3) {
            return configurationSerializableFromString(object.get("encoded").getAsString());
        } else if(type == 4) {
            return WrappedBlockData.createData(Material.valueOf(object.get("type").getAsString()), object.get("data").getAsInt());
        } else if(type == 5) {
            return new BlockPosition(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt());
        } else if(type == 6) {
            return EnumWrappers.Direction.valueOf(object.get("direction").getAsString());
        } else if(type == 7) {
            return NbtTextSerializer.DEFAULT.deserializeCompound(object.get("encoded").getAsString());
        } else if(type == 8) {
            return new WrappedChunkCoordinate(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt());
        } else if(type == 9) {
            return new ChunkPosition(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt());
        } else {
            return null;
        }
    }

    protected ConfigurationSerializable configurationSerializableFromString(String encoded) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(BaseEncoding.base64().decode(encoded));

        try (BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream)) {
            return (ConfigurationSerializable) bukkitInputStream.readObject();
        }
    }
}
