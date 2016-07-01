package xyz.openmodloader.test;

import java.util.Arrays;
import java.util.Objects;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.impl.*;
import xyz.openmodloader.network.Channel;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

public class OMLServerDelegate {
    public Channel channel;
    public void registerEvents(Channel channel) {
        this.channel = channel;

        OpenModLoader.getEventBus().register(BlockEvent.Place.class, this::onBlockPlace);
        OpenModLoader.getEventBus().register(BlockEvent.Destroy.class, this::onBlockDestroy);
        OpenModLoader.getEventBus().register(BlockEvent.DigSpeed.class, this::onBlockDigSpeed);
        OpenModLoader.getEventBus().register(BlockEvent.HarvestDrops.class, this::onHarvestDrops);

        OpenModLoader.getEventBus().register(EnchantmentEvent.Item.class, this::onItemEnchanted);
        OpenModLoader.getEventBus().register(EnchantmentEvent.Level.class, this::onEnchantmentLevelCheck);

        OpenModLoader.getEventBus().register(ExplosionEvent.class, this::onExplosion);

        OpenModLoader.getEventBus().register(CommandEvent.class, this::onCommandRan);

        OpenModLoader.getEventBus().register(EntityEvent.Constructing.class, this::onEntityConstruct);
        OpenModLoader.getEventBus().register(EntityEvent.Join.class, this::onEntityJoinWorld);

        OpenModLoader.getEventBus().register(ArmorEvent.Equip.class, this::onArmorEquip);
        OpenModLoader.getEventBus().register(ArmorEvent.Unequip.class, this::onArmorUnequip);

        OpenModLoader.getEventBus().register(EntityEvent.ChangeDimension.class, this::onChangeDimension);

        OpenModLoader.getEventBus().register(EntityEvent.Mount.class, this::onMount);
        OpenModLoader.getEventBus().register(EntityEvent.Dismount.class, this::onDismount);

        OpenModLoader.getEventBus().register(EntityEvent.LightningStruck.class, this::onLightningStrike);

        OpenModLoader.getEventBus().register(PlayerEvent.Craft.class, this::onCraft);
        OpenModLoader.getEventBus().register(PlayerEvent.Smelt.class, this::onSmelt);
        OpenModLoader.getEventBus().register(PlayerEvent.ItemPickup.class, this::onPickup);
        OpenModLoader.getEventBus().register(PlayerEvent.SleepCheck.class, this::onSleepCheck);

        OpenModLoader.getEventBus().register(PlayerEvent.Track.Start.class, this::onStartTracking);
        OpenModLoader.getEventBus().register(PlayerEvent.Track.Stop.class, this::onStopTracking);
    }
    private void onBlockPlace(BlockEvent.Place event) {
        OpenModLoader.getLogger().info("Placed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setBlockState(Blocks.DIRT.getDefaultState());
        } if (event.getWorld() instanceof WorldServer && event.getBlockState().getBlock() == Blocks.BEDROCK) {
            channel.send("test")
                    .set("str", "Hello, Client!")
                    .toAllInRadius((WorldServer)event.getWorld(), event.getPos(), 8);
        }
    }

    private void onBlockDestroy(BlockEvent.Destroy event) {
        OpenModLoader.getLogger().info("Destroyed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setCanceled(true);
        }
    }

    private void onBlockDigSpeed(BlockEvent.DigSpeed event) {
        if (event.getBlockState().getBlock() == Blocks.DIRT) {
            event.setDigSpeed(0.05F);
        }
    }



    private void onItemEnchanted(EnchantmentEvent.Item event) {
        OpenModLoader.getLogger().info(event.getItemStack().getDisplayName() + " " + event.getEnchantments().toString());
    }

    private void onEnchantmentLevelCheck(EnchantmentEvent.Level event) {
        if (event.getEnchantment() == Enchantments.FORTUNE && event.getEntityLiving().isSneaking()) {
            int oldLevel = event.getLevel();
            int newLevel = (oldLevel + 1) * 10;
            event.setLevel(newLevel);
            OpenModLoader.getLogger().info("Set fortune level from " + oldLevel + " to " + newLevel);
        }
    }

    private void onExplosion(ExplosionEvent event) {
        event.setCanceled(true);
    }

    private void onHarvestDrops(BlockEvent.HarvestDrops event) {
        OpenModLoader.getLogger().info("Dropping items: " + event.getDrops() + ", with fortune: " + event.getFortune() + ", with chance: " + event.getChance());
        event.getDrops().add(new ItemStack(Blocks.DIRT));
    }

    private void onCommandRan(CommandEvent event) {
        OpenModLoader.getLogger().info("Player: " + event.getSender().getName() + " ran command: " + event.getCommand().getCommandName() + " with arguments: " + Arrays.toString(event.getArgs()));
    }

    private void onEntityConstruct(EntityEvent.Constructing event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.getLogger().info("A player was constructed.");
        }
    }

    private void onEntityJoinWorld(EntityEvent.Join event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.getLogger().info(String.format("A player joined the world on side %s.", event.getWorld().isRemote ? "client" : "server"));
        }
        if (event.getEntity() instanceof EntityPig) {
            event.setCanceled(true);
        }
    }

    private void onArmorEquip(ArmorEvent.Equip event){
        OpenModLoader.getLogger().info("Entity: " + event.getEntity().getName() + " equipped " + Objects.toString(event.getArmor()) + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onArmorUnequip(ArmorEvent.Unequip event){
        OpenModLoader.getLogger().info("Entity: " + event.getEntity().getName() + " unequipped " + Objects.toString(event.getArmor()) + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onChangeDimension(EntityEvent.ChangeDimension event) {
        OpenModLoader.getLogger().info("Entity: %s is travelling from dimension %d to %d", event.getEntity(), event.getPreviousDimension(), event.getNewDimension());
        if (event.getNewDimension() == -1) {
            event.setNewDimension(1);
        }
    }

    private void onMount(EntityEvent.Mount event) {
        if (event.getRiding() instanceof EntityPig) {
            event.setCanceled(true);
        }
    }

    private void onDismount(EntityEvent.Dismount event) {
        if (event.getRiding() instanceof EntityHorse) {
            event.setCanceled(true);
        }
    }

    private void onLightningStrike(EntityEvent.LightningStruck event) {
        if (event.getEntity() instanceof EntityCreeper) {
            event.setCanceled(true);
        }
    }

    private void onCraft(PlayerEvent.Craft event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " crafted " + event.getResult());
    }

    private void onSmelt(PlayerEvent.Smelt event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " smelted " + event.getResult());
        if (event.getResult().getItem() == Items.IRON_INGOT) {
            event.setXP(1.0F);
        }
    }

    private void onPickup(EntityEvent.ItemPickup event) {
        if (event.getItem().getEntityItem().getItem() == Items.APPLE) {
            event.setCanceled(true);
        }
    }

    private void onStartTracking(PlayerEvent.Track.Start event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " started tracking " + event.getTracking());
    }

    private void onStopTracking(PlayerEvent.Track.Stop event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " stopped tracking " + event.getTracking());
    }

    private void onSleepCheck(PlayerEvent.SleepCheck event) {
        OpenModLoader.getLogger().info("Sleep check occurred for %s at %s, default result is %s", event.getPlayer(), event.getPos(), event.getResult());
    }
}
