package xyz.openmodloader.registry;

import xyz.openmodloader.OpenModLoader;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class GameRegistry {
    public static void registerBlock(Block block, ResourceLocation identifier) {
        registerBlock(block, new ItemBlock(block), identifier);
    }

    public static void registerBlock(Block block, ItemBlock itemBlock, ResourceLocation identifier) {
        NamespacedRegistry<ResourceLocation, Block> blockRegistry = OMLRegistry.getRegistry(Block.class);
        NamespacedRegistry<ResourceLocation, Item> itemRegistry = OMLRegistry.getRegistry(Item.class);
        if (block == null || itemBlock == null) {
            OpenModLoader.getLogger().warn("The Block or ItemBlock cannot be null");
            return;
        }

        if (identifier == null) {
            OpenModLoader.getLogger().warn("The Block Identifier cannot be null");
            return;
        }
        if (blockRegistry.containsKey(identifier)) {
            OpenModLoader.getLogger().warn("The ID %s has already been registered for %s. It will not be registered again.", identifier, blockRegistry.getObject(identifier));
            return;
        }

        blockRegistry.register(identifier, block);
        for (IBlockState state : block.getBlockState().getValidStates()) {
            Block.BLOCK_STATE_IDS.put(state, OMLRegistry.getRegistry(Block.class).getIDForObject(block) << 4 | block.getMetaFromState(state));
        }

        itemRegistry.register(Block.getIdFromBlock(itemBlock.getBlock()), Block.REGISTRY.getNameForObject(itemBlock.getBlock()), itemBlock);
        OMLRegistry.getBlockItemMap().put(block, itemBlock);
    }

    public static void registerItem(Item item, ResourceLocation identifier) {
        NamespacedRegistry<ResourceLocation, Item> itemRegistry = OMLRegistry.getRegistry(Item.class);

        if (item == null) {
            OpenModLoader.getLogger().warn("The Item cannot be null");
            return;
        }

        if (identifier == null) {
            OpenModLoader.getLogger().warn("The Block Identifier cannot be null");
            return;
        }
        if (itemRegistry.containsKey(identifier)) {
            OpenModLoader.getLogger().warn("The ID %s has already been registered for %s. It will not be registered again.", identifier, itemRegistry.getObject(identifier));
            return;
        }
        itemRegistry.register(identifier, item);
    }
}
