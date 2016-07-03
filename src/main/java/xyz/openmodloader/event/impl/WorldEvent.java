package xyz.openmodloader.event.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * Parent class for world related events. All events that fall within this scope
 * should extend this class. They should usually also be added as inner classes,
 * although this is not an absolute requirement.
 */
public abstract class WorldEvent extends Event {

    /**
     * The world that this event was fired from
     */
    private final World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    /**
     * Gets the world that this event was fired from.
     * 
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Fired when the world has loaded. Fired from {@link WorldServer#init()}
     * and the {@link WorldClient} constructor.
     * 
     * This event is not cancelable.
     */
    public static class Load extends WorldEvent {

        public Load(World world) {
            super(world);
        }

        public static void handle(World world) {
            OpenModLoader.getEventBus().post(new Load(world));
        }
    }

    /**
     * Fired when the world has unloaded. Fired from
     * {@link Minecraft#loadWorld(WorldClient,String)} and
     * {@link MinecraftServer#stopServer()}.
     * 
     * This event is not cancelable.
     */
    public static class Unload extends WorldEvent {

        public Unload(World world) {
            super(world);
        }

        public static void handle(World world) {
            OpenModLoader.getEventBus().post(new Unload(world));
        }
    }

    /**
     * Fired when the spawn point is determined. Fired from
     * {@link WorldServer#createSpawnPosition(WorldSettings)}.
     * 
     * This event is cancelable.
     */
    public static class SetSpawn extends WorldEvent {

        /**
         * The position that will be used as spawn point.
         */
        private BlockPos spawnPoint;

        public SetSpawn(World world, BlockPos spawnPoint) {
            super(world);
            this.spawnPoint = spawnPoint;
        }

        /**
         * Gets the position that will be used as spawn point.
         * 
         * @return the position
         */
        public BlockPos getSpawnPoint() {
            return spawnPoint;
        }

        /**
         * Sets the position that will be used as spawn point
         * 
         * @param spawnPoint the new spawn point
         */
        public void setSpawnPoint(BlockPos spawnPoint) {
            this.spawnPoint = spawnPoint;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        public static SetSpawn handle(World world, BlockPos spawnPoint) {
            SetSpawn event = new SetSpawn(world, spawnPoint);
            OpenModLoader.getEventBus().post(event);
            return event;
        }
    }
}