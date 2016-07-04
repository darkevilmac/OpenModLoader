package xyz.openmodloader.world.generation;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import xyz.openmodloader.registry.NamespacedRegistry;
import xyz.openmodloader.registry.OMLRegistry;

/**
 * Helper class for world generation hooks.
 */
public final class WorldGenHooks {

    private static final NamespacedRegistry<ResourceLocation, WorldGenerator> generation = OMLRegistry.getRegistry(WorldGenerator.class);

    /**
     * Generates features for a chunk. This should be called from custom
     * {@link BiomeDecorator}s that override the default
     * {@link BiomeDecorator#decorate(World, Random, Biome, BlockPos)} method
     * without calling {@code super.decorate()}.
     *
     * @param biome the biome
     * @param world the world
     * @param random the random
     * @param chunkPos the chunk pos
     */
    public static void runGenerators(Biome biome, World world, Random random, BlockPos chunkPos) {
        for (WorldGenerator feature : generation) {
            feature.generate(biome, world, random, chunkPos);
        }
    }
}