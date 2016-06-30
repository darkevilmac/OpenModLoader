package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * {@link Channel} builder
 */
public class ChannelBuilder {

    final String name;
    final BiMap<String, PacketSpec> specs;
    final BiMap<String, Integer> specIDs;

    ChannelBuilder(String name) {
        this.name = name;
        this.specs = HashBiMap.create();
        this.specIDs = HashBiMap.create();
    }

    /**
     * Creates a new {@link PacketSpecBuilder} to build a new packet spec
     * @param name The name of the new packet
     * @return The packet spec builder
     */
    public PacketSpecBuilder createPacket(String name) {
        return new PacketSpecBuilder(this, name);
    }

    void addPacket(PacketSpec spec) {
        specs.put(spec.name, spec);
    }

    /**
     * Builds this channel and registers it with the {@link ChannelManager}
     * @return The finalized channel
     */
    public Channel build() {
        Channel channel = new Channel(this);
        ChannelManager.register(name, channel);
        return channel;
    }


}
