package xyz.openmodloader.registry;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldGenMinable;
import xyz.openmodloader.world.generation.WorldGenerator;

/**
 * The anchor point for registering content. Blocks, items, world generators,
 * etc are registered here.
 */
public class GameRegistry {

    /**
     * Initializes the registry.
     */
    public static void init() {
        registerHorseArmor(new ResourceLocation("none"), HorseArmorType.NONE);
        registerHorseArmor(new ResourceLocation("iron"), HorseArmorType.IRON);
        registerHorseArmor(new ResourceLocation("gold"), HorseArmorType.GOLD);
        registerHorseArmor(new ResourceLocation("diamond"), HorseArmorType.DIAMOND);
    }

    /**
     * Registers a new horse armor.
     *
     * @param identifier the identifier
     * @param horseArmor the horse armor
     */
    public static void registerHorseArmor(ResourceLocation identifier, HorseArmorType horseArmor) {
        OMLRegistry.getRegistry(HorseArmorType.class).register(identifier, horseArmor);
    }

    /**
     * Registers a block.
     *
     * @param identifier the identifier - must be unique
     * @param block the block
     */
    public static void registerBlock(ResourceLocation identifier, Block block) {
        registerBlock(identifier, block, new ItemBlock(block));
    }

    /**
     * Registers a block.
     *
     * @param identifier the identifier - must be unique
     * @param block the block
     * @param itemBlock the item block
     */
    public static void registerBlock(ResourceLocation identifier, Block block, ItemBlock itemBlock) {
        NamespacedRegistry<ResourceLocation, Block> blockRegistry = OMLRegistry.getRegistry(Block.class);
        NamespacedRegistry<ResourceLocation, Item> itemRegistry = OMLRegistry.getRegistry(Item.class);
        if (block == null || itemBlock == null) {
            throw new NullPointerException("Neither the block nor the itemblock may be null");
        } else if (identifier == null) {
            throw new NullPointerException("The identifier cannot be null!");
        } else if (blockRegistry.containsKey(identifier)) {
            throw new RuntimeException(String.format("The ID %s has already been registered for %s. It will not be registered again.", identifier, blockRegistry.getObject(identifier)));
        } else if (itemRegistry.containsKey(identifier)) {
            throw new RuntimeException(String.format("The ID %s has already been registered for %s. It will not be registered again.", identifier, itemRegistry.getObject(identifier)));
        }
        blockRegistry.register(identifier, block);
        for (IBlockState state : block.getBlockState().getValidStates()) {
            Block.BLOCK_STATE_IDS.put(state, OMLRegistry.getRegistry(Block.class).getIDForObject(block) << 4 | block.getMetaFromState(state));
        }
        itemRegistry.register(Block.getIdFromBlock(itemBlock.getBlock()), Block.REGISTRY.getNameForObject(itemBlock.getBlock()), itemBlock);
        OMLRegistry.getBlockItemMap().put(block, itemBlock);
    }

    /**
     * Register item.
     *
     * @param identifier the identifier
     * @param item the item
     */
    public static void registerItem(ResourceLocation identifier, Item item) {
        NamespacedRegistry<ResourceLocation, Item> itemRegistry = OMLRegistry.getRegistry(Item.class);
        if (item == null) {
            throw new NullPointerException("The item cannot be null");
        } else if (identifier == null) {
            throw new NullPointerException("The identifier cannot be null!");
        } else if (itemRegistry.containsKey(identifier)) {
            throw new RuntimeException(String.format("The ID %s has already been registered for %s. It will not be registered again.", identifier, itemRegistry.getObject(identifier)));
        }
        itemRegistry.register(identifier, item);
    }

    /**
     * Registers a world generator.
     *
     * @param identifier the identifier
     * @param gen the generation
     */
    public static void registerWorldGen(ResourceLocation identifier, WorldGenerator gen) {
        NamespacedRegistry<ResourceLocation, WorldGenerator> registry = OMLRegistry.getRegistry(WorldGenerator.class);
        if (gen == null) {
            throw new NullPointerException("The generator cannot be null");
        } else if (identifier == null) {
            throw new NullPointerException("The identifier cannot be null!");
        } else if (registry.containsKey(identifier)) {
            throw new RuntimeException(String.format("The ID %s has already been registered for %s. It will not be registered again.", identifier, registry.getObject(identifier)));
        }
        registry.register(identifier, gen);
    }

    /**
     * Registers an ore generator to the world generator registry.
     *
     * @param id the generator id
     * @param ore the ore
     * @param replaceables the blocks the ore may spawn in, usually stone for
     *        the overworld and netherrack for the nether
     * @param veinType the vein type
     * @param veinSize the vein size
     * @param minY the minimum Y level the ore may spawn at
     * @param maxY the maximum Y level the ore may spawn at
     * @param dimensions the dimensions the ore may spawn in - may be
     *        {@link Short#MAX_VALUE} as a wildcard
     * @param attempts the number of attempts at spawning the ore per chunk
     * @see OreVeinType
     */
    public static void registerOreGen(ResourceLocation id, IBlockState ore, IBlockState[] replaceables, int veinSize, int minY, int maxY, int[] dimensions, int attempts) {
        Set<IBlockState> replaceables0 = ImmutableSet.copyOf(replaceables);
        int[] dimensions0 = Arrays.copyOf(dimensions, dimensions.length);
        WorldGenMinable mineable = new WorldGenMinable(ore, veinSize, (b) -> replaceables0.contains(b));
        registerWorldGen(id, (biome, world, random, chunkPos) -> {
            for (int dim : dimensions0) {
                if (dim != world.provider.getDimensionType().getId() && dim != Short.MAX_VALUE) {
                    continue;
                }
                for (int attempt = 0; attempt < attempts; attempt++) {
                    BlockPos pos = chunkPos.add(world.rand.nextInt(16), world.rand.nextInt(maxY - minY + 1) + minY, world.rand.nextInt(16));
                    mineable.generate(world, world.rand, pos);
                }
                return;
            }
        });
    }
}
