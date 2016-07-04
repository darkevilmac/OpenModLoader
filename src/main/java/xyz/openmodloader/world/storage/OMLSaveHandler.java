package xyz.openmodloader.world.storage;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.WorldInfo;
import xyz.openmodloader.registry.NamespacedRegistry;
import xyz.openmodloader.registry.OMLRegistry;

public class OMLSaveHandler {

    public static void save(WorldInfo worldInfo, NBTTagCompound nbt) {
        final NBTTagList registryList = new NBTTagList();

        final Map<String, NamespacedRegistry<ResourceLocation, Object>> registryMap = OMLRegistry.getRegistryMap();
        for (final String types : registryMap.keySet()) {
            final NBTTagCompound registryType = new NBTTagCompound();
            registryType.setString("type", types);

            NBTTagList registeredList = new NBTTagList();
            for (ResourceLocation key : registryMap.get(types).getKeys()) {
                NBTTagCompound registeredObject = new NBTTagCompound();
                registeredObject.setString("key", key.toString());
                registeredObject.setInteger("value", registryMap.get(types).getIDForObject(registryMap.get(types).getObject(key)));
                registeredList.appendTag(registeredObject);
            }
            registryType.setTag("Data", registeredList);
            registryList.appendTag(registryType);
        }
        nbt.setTag("Registry", registryList);
    }

    public static void load(NBTTagCompound nbt) {
        NBTTagList registryList = nbt.getTagList("Registry", 10);

        final Map<String, NamespacedRegistry<ResourceLocation, Object>> registryMap = OMLRegistry.getRegistryMap();
        for (int i = 0; i < registryList.tagCount(); ++i) {
            NBTTagCompound registryType = registryList.getCompoundTagAt(i);
            if (registryMap.containsKey(registryType.getString("type"))) {
                NamespacedRegistry<ResourceLocation, Object> registeredList = registryMap.get(registryType.getString("type"));
                NBTTagList data = registryType.getTagList("Data", 10);

                Map<ResourceLocation, Integer> resourceLocationIntegerMap = new HashMap<>();
                for (int j = 0; j < data.tagCount(); ++j) {
                    NBTTagCompound registed = data.getCompoundTagAt(j);
                    resourceLocationIntegerMap.put(new ResourceLocation(registed.getString("key")), registed.getInteger("value"));
                }
                registeredList.set(resourceLocationIntegerMap);
            }
        }
    }

    public static void writeChunkToNBT(Chunk chunk, World world, NBTTagCompound nbt) {
        NBTTagList var5 = new NBTTagList();
        for (ExtendedBlockStorage var10 : chunk.getBlockStorageArray()) {
            if (var10 != Chunk.NULL_BLOCK_STORAGE) {
                NBTTagCompound var11 = new NBTTagCompound();
                var11.setByte("Y", (byte) (var10.getYLocation() >> 4 & 255));
                int[] blockIDs = new int[4096];
                byte[] states = new byte[4096];
                for (int i = 0; i < 4096; ++i) {

                    int x = i & 15;
                    int y = i >> 8 & 15;
                    int z = i >> 4 & 15;

                    int blockID = Block.BLOCK_STATE_IDS.get(var10.get(x, y, z));

                    ResourceLocation identifier = OMLRegistry.getRegistry(Block.class).getNameForObject(var10.get(x, y, z).getBlock());
                    if (identifier == null || !identifier.getResourceDomain().equalsIgnoreCase("minecraft")) {
                        blockIDs[i] = blockID >> 4;
                        states[i] = (byte) (blockID & 15);
                    } else {
                        blockIDs[i] = -1;
                        states[i] = -1;
                    }
                }
                var11.setIntArray("Blocks", blockIDs);
                var11.setByteArray("Data", states);
                var5.appendTag(var11);
            }
        }

        nbt.setTag("OMLBlocks", var5);
    }

    public static Chunk readChunkFromNBT(World world, NBTTagCompound nbt, Chunk chunk) {
        NBTTagList var6 = nbt.getTagList("OMLBlocks", 10);

        for (int var10 = 0; var10 < var6.tagCount(); ++var10) {
            NBTTagCompound var11 = var6.getCompoundTagAt(var10);
            byte var12 = var11.getByte("Y");
            ExtendedBlockStorage var13 = null;
            for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) {
                if (storage.getYLocation() == (var12 << 4)) {
                    var13 = storage;
                    break;
                }
            }
            if (var13 == null) {
                throw new RuntimeException("Chunk Loading Failed");
            }

            int[] blockIDs = var11.getIntArray("Blocks");
            byte[] states = var11.getByteArray("Data");
            for (int i = 0; i < 4096; ++i) {

                int x = i & 15;
                int y = i >> 8 & 15;
                int z = i >> 4 & 15;
                if (states[i] != -1) {
                    int blockID = (blockIDs[i] << 4) + states[i];
                    IBlockState state = Block.BLOCK_STATE_IDS.getByValue(blockID);
                    if (state == null) {
                        state = Block.BLOCK_STATE_IDS.getByValue(0);
                    }
                    var13.set(x, y, z, state);
                }
            }

            var13.removeInvalidBlocks();
        }

        return chunk;
    }
}
