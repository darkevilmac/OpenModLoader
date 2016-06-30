package xyz.openmodloader.network;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Packet specification builder
 */
public class PacketSpecBuilder {

    private final ChannelBuilder builder;
    final String name;
    final Map<String, DataType> types;
    BiConsumer<Context, Packet> handler;

    PacketSpecBuilder(ChannelBuilder builder, String name) {
        this.builder = builder;
        this.name = name;
        this.types = new HashMap<>();
    }

    /**
     * Adds a data type with the given ID to the allowed data types for this packet
     * @param id The ID to add the data type as
     * @param type The type to be added
     * @return This spec builder
     */
    public PacketSpecBuilder with(String id, DataType type) {
        if (types.containsKey(id)) throw new IllegalArgumentException(String.format("ID %s is already set to DataType %s", id, types.get(id)));

        types.put(id, type);

        return this;
    }

    /**
     * Sets the handler for this packet spec and adds the finalized spec to the {@link ChannelBuilder}
     * This handler will be executed on the Netty I/O thread.
     * @param handler The packet handler
     * @return The channel builder
     */
    public ChannelBuilder handle(BiConsumer<Context, Packet> handler) {
        if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", name, this.handler));

        this.handler = handler;
        builder.addPacket(new PacketSpec(this));

        return builder;
    }

    /**
     * Sets the handler for this packet spec and adds the finalized spec to the {@link ChannelBuilder}
     * These handlers will be executed on the Netty I/O thread.
     * @param clientHandler The client-side handler
     * @param serverHandler The server-side handler
     * @return The channel builder
     */
    public ChannelBuilder handle(BiConsumer<Context, Packet> clientHandler, BiConsumer<Context, Packet> serverHandler) {
        if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", name, this.handler));

        this.handler = (context, packet) -> {
            if (context.getSide() == Side.CLIENT) {
                clientHandler.accept(context, packet);
            } else if (context.getSide() == Side.SERVER) {
                serverHandler.accept(context, packet);
            }
        };

        builder.addPacket(new PacketSpec(this));

        return builder;
    }

    /**
     * Sets the handler for this packet spec and adds the finalized spec to the {@link ChannelBuilder}
     * This handler will be executed on the main Minecraft thread
     * @param handler The handler
     * @return The channel builder
     */
    public ChannelBuilder handleOnMainThread(BiConsumer<Context, Packet> handler) {
        return handle((context, packet) -> {
            OpenModLoader.getSidedHandler().handleOnMainThread(() -> {
                handler.accept(context, packet);
            });
        });
    }

    /**
     * Sets the handler for this packet spec and adds the finalized spec to the {@link ChannelBuilder}
     * These handlers will be executed on the main Minecraft thread.
     * @param clientHandler The client-side handler
     * @param serverHandler The server-side handler
     * @return The channel builder
     */
    public ChannelBuilder handleOnMainThread(BiConsumer<Context, Packet> clientHandler, BiConsumer<Context, Packet> serverHandler) {
        return handle((context, packet) -> {
            if (context.getSide() == Side.CLIENT) {
                OpenModLoader.getSidedHandler().handleOnMainThread(() -> {
                    clientHandler.accept(context, packet);
                });
            } else if (context.getSide() == Side.SERVER) {
                OpenModLoader.getSidedHandler().handleOnMainThread(() -> {
                    serverHandler.accept(context, packet);
                });
            }
        });
    }

}
