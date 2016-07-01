package xyz.openmodloader.network;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.Side;

/**
 * The context in which this packet is being handled
 */
public class PacketContext {

    private final Side side;

    /**
     * Default context constructor, using the physical side
     */
    public PacketContext() {
        this(OpenModLoader.getSidedHandler().getSide());
    }

    public PacketContext(Side side) {
        this.side = side;
    }

    /**
     * @return The side the packing is being handled on
     */
    public Side getSide() {
        return side;
    }

}
