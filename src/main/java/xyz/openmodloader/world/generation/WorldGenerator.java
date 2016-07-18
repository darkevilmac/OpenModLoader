package xyz.openmodloader.world.generation;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface WorldGenerator {

    /**
     * Generates the feature.
     *
     * @param biome the biome
     * @param world the world
     * @param random the random to use for number generation
     * @param chunkPos the position of the chunk to generate in
     */
    void generate(Biome biome, World world, Random random, BlockPos chunkPos);
}