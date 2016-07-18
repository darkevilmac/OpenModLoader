package xyz.openmodloader.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * Parent class for generic update events. All events that fall within this
 * scope should extend this class. They should also be added as an inner class
 * however this is not required.
 */
public class UpdateEvent extends Event {

    /**
     * Fired every time the world has an update tick.
     */
    public static class WorldUpdate extends UpdateEvent {

        /**
         * The world that has updated.
         */
        private World world;

        /**
         * Constructs a new event that is fired when the world has an update
         * tick.
         *
         * @param world The world that has updated.
         */
        public WorldUpdate(World world) {
            this.world = world;
        }

        /**
         * Gets the world that has updated.
         *
         * @return The world that has updated.
         */
        public World getWorld() {
            return world;
        }
    }

    /**
     * Fired every time an entity has an update tick.
     */
    public static class EntityUpdate extends UpdateEvent {

        /**
         * The entity that has updated.
         */
        private final Entity entity;

        /**
         * Constructs a new event that is fired when an entity has an update
         * tick.
         *
         * @param entity The entity that has updated.
         */
        public EntityUpdate(Entity entity) {
            this.entity = entity;
        }

        /**
         * Gets the entity that updated.
         *
         * @return The entity that updated.
         */
        public Entity getEntity() {
            return entity;
        }
    }

    /**
     * Fired when there is a render update.
     */
    public static class RenderUpdate extends UpdateEvent {

        /**
         * The partial ticks for the update.
         */
        private final float partialTicks;

        /**
         * Constructs a new event that is fired every render update.
         *
         * @param partialTicks The partial ticks for the update.
         */
        public RenderUpdate(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        /**
         * Gets the partial ticks for the update.
         *
         * @return The partial ticks for the update.
         */
        public float getPartialTicks() {
            return partialTicks;
        }
    }

    /**
     * Fired every client update.
     */
    public static class ClientUpdate extends UpdateEvent {

    }

    /**
     * Fired every server update.
     */
    public static class ServerUpdate extends UpdateEvent {

        /**
         * The server instance;
         */
        private final MinecraftServer server;

        public ServerUpdate(MinecraftServer server) {
            this.server = server;
        }

        /**
         * Gets the server instance
         *
         * @return The server instance
         */
        public MinecraftServer getServer() {
            return server;
        }
    }

    /**
     * Fired every time a player's armor stack is ticked.
     */
    public static class ArmorUpdate extends UpdateEvent {

        /**
         * The player whose armor is being ticked.
         *
         * @see #getPlayer()
         */
        private final EntityPlayer player;

        /**
         * The stack that contains the armor.
         *
         * @see #getStack()
         */
        private final ItemStack stack;

        /**
         * Creates a new instance of the armor update event.
         * 
         * @param player The player whose armor is being ticked
         * @param stack The stack containing the armor being ticked
         */
        private ArmorUpdate(EntityPlayer player, ItemStack stack) {
            this.player = player;
            this.stack = stack;
        }

        /**
         * Gets the player whose armor is being ticked.
         *
         * @return The player whose armor is being ticked
         */
        public EntityPlayer getPlayer() {
            return player;
        }

        /**
         * Gets the armor stack that is being ticked.
         *
         * @return The stack that contains the armor
         */
        public ItemStack getStack() {
            return stack;
        }

        /**
         * Internal handle method that posts the event and invokes the
         * {@link net.minecraft.item.Item#onArmorTick(EntityPlayer, ItemStack)}
         * method of the armor
         * 
         * @param player The player whose armor is being ticked
         * @param stack The stack containing the armor being ticked
         */
        public static void handle(EntityPlayer player, ItemStack stack) {
            OpenModLoader.getEventBus().post(new ArmorUpdate(player, stack));
            stack.getItem().onArmorTick(player, stack);
        }
    }
}
