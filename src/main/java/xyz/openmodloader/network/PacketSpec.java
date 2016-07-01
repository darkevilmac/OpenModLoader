package xyz.openmodloader.network;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The specification for a packet.
 */
public class PacketSpec {

    final String name;
    final Map<String, DataType> types;
    BiConsumer<PacketContext, Packet> handler;

    /**
     * Creates a finalized immutable packet from the builder
     * @param builder The packet spec builder
     */
    PacketSpec(PacketSpecBuilder builder) {
        this.name = builder.name;
        this.types = ImmutableMap.copyOf(builder.types);
        this.handler = builder.handler;
    }

}
