package xyz.openmodloader.event.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.impl.PlayerEvent;

public class EventSetSpawn extends PlayerEvent {
    private BlockPos newSpawn, oldSpawn;
    private boolean forced;

    /**
     * Constructor for a player spawn set event,
     * accessed through super calls.
     *
     * @param player The player that has fired this event.
     * @param oldSpawn The player's previous spawn point.
     * @param newSpawn The new spawn point that was set.
     * @param force Whether or not the players new spawn point is forced.
     */
    public EventSetSpawn(EntityPlayer player, BlockPos oldSpawn, BlockPos newSpawn, boolean force) {
        super(player);
        this.oldSpawn = oldSpawn;
        this.newSpawn = newSpawn;
        this.forced = force;
    }

    public static EventSetSpawn handle(EntityPlayer player, BlockPos oldSpawn, BlockPos newSpawn, boolean force) {
        EventSetSpawn event = new EventSetSpawn(player, oldSpawn, newSpawn, force);
        OpenModLoader.getEventBus().post(event);
        return event;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /**
     * Gets the value of the new spawn position for the player.
     *
     * @return The value of the new spawn position for the player.
     */
    public BlockPos getNewSpawn() {
        return newSpawn;
    }

    /**
     * Adjusts the value of the new spawn position for the player.
     *
     * @param newSpawn The value of the new spawn position you want to set.
     */
    public void setNewSpawn(BlockPos newSpawn) {
        this.newSpawn = newSpawn;
    }

    /**
     * Gets the future value of spawnForced that will be set to the player.
     *
     * @return The value of spawnForced that will be set to the player.
     */
    public boolean isForced() {
        return forced;
    }

    /**
     * Adjusts the value of spawnForced that will be set to the player.
     *
     * @param forced The new value of spawnForced that will be set to the player.
     */
    public void setForced(boolean forced) {
        this.forced = forced;
    }

    /**
     * Gets the player's previous spawn.
     *
     * @return The player's previous spawn.
     */
    public BlockPos getOldSpawn() {
        return oldSpawn;
    }
}
