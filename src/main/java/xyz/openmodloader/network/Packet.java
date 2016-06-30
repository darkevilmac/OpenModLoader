package xyz.openmodloader.network;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.PacketBuffer;

import java.util.Map;

/**
 * Default OML packet implementation.
 */
public class Packet extends AbstractPacket {

    final PacketSpec spec;
    private final Map<String, Object> values;
    private final Map<String, DataType> types;

    Packet(PacketBuilder builder) {
        this.spec = builder.spec;
        this.values = ImmutableMap.copyOf(builder.values);
        this.types = ImmutableMap.copyOf(builder.types);
    }

    /**
     * Retrieves a value from this packet
     * @param id The ID of the thing being retrieved
     * @param type The {@link DataType} type of the thing being stored
     * @param <T> The type of the value
     * @return The value stored
     */
    public <T> T get(String id, DataType<T> type) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
        if (!types.get(id).equals(type)) throw new IllegalArgumentException(String.format("Wrong type for key %s, %s is registered but got %s", id, types.get(id), type));
        return (T)values.get(id);
    }

    /**
     * Writes this packet to the given buffer
     * @param buf The buffer
     * @return The buffer for convienience
     */
    @Override
    public PacketBuffer write(PacketBuffer buf) {
        values.forEach((id, value) -> {
            types.get(id).write(buf, value);
        });
        return buf;
    }

    /**
     * Reads this packet from the given buffer
     * @param buf The buffer
     */
    @Override
    public void read(PacketBuffer buf) {
        types.forEach((id, type) -> {
            values.put(id, type.read(buf));
        });
    }

    /**
     * Handles receiving this packet
     */
    @Override
    public void handle() {
        spec.handler.accept(new Context(), this);
    }

}
