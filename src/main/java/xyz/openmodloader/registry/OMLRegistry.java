package xyz.openmodloader.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class OMLRegistry {
    private static Map<Block, Item> blockItemMap;
    private static Map<String, NamespacedRegistry<ResourceLocation, Object>> REGISTRY_MAP = new HashMap<>();

    public static <T> NamespacedRegistry<ResourceLocation, T> getRegistry(Class<T> classRegistry) {
        return getRegistry(classRegistry, new ResourceLocation("air"));
    }

    public static <T> NamespacedRegistry<ResourceLocation, T> getRegistry(Class<T> classRegistry, ResourceLocation defaultKey) {
        if (!REGISTRY_MAP.containsKey(classRegistry.getName())) {
            registerRegistry(classRegistry, defaultKey);
        }

        return (NamespacedRegistry<ResourceLocation, T>) REGISTRY_MAP.get(classRegistry.getName());
    }


    public static NamespacedRegistry<ResourceLocation, ?> registerRegistry(Class<?> classRegistry, ResourceLocation defaultKey) {
        REGISTRY_MAP.put(classRegistry.getName(), new NamespacedRegistry<>(defaultKey));

        return REGISTRY_MAP.get(classRegistry.getName());
    }

    public static Map<Block, Item> getBlockItemMap() {
        if (blockItemMap == null) {
            blockItemMap = new HashMap<>();
        }

        return blockItemMap;
    }

    public static Map<String, NamespacedRegistry<ResourceLocation, Object>> getRegistryMap() {
        return REGISTRY_MAP;
    }
}
