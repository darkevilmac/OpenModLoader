package xyz.openmodloader.registry;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.registry.RegistrySimple;

public class NamespacedRegistry<ResourceLocation, V> extends RegistrySimple<ResourceLocation, V> implements IObjectIntIterable<V> {
    protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap<>(Short.MAX_VALUE - 1);
    protected final Map<V, ResourceLocation> inverseObjectRegistry;

    private BitSet availableIds;

    private final ResourceLocation defaultValueKey;
    private V defaultValue;

    public NamespacedRegistry(ResourceLocation var1) {
        this.defaultValueKey = var1;
        this.inverseObjectRegistry = ((BiMap) this.registryObjects).inverse();
        this.availableIds = new BitSet(Short.MAX_VALUE - 1);
    }

    public void register(ResourceLocation var2, V var3) {
        if (this.defaultValueKey.equals(var2)) {
            this.defaultValue = var3;
        }

        int id = availableIds.nextClearBit(0);
        this.availableIds.set(id);
        set(id, var2, var3);
    }

    public void register(int id, ResourceLocation var2, V var3) {
        if (this.defaultValueKey.equals(var2)) {
            this.defaultValue = var3;
        }

        if (id < 0 || availableIds.get(id)) {
            id = availableIds.nextClearBit(0);
        }
        this.availableIds.set(id);
        set(id, var2, var3);
    }

    public void set(int id, ResourceLocation var2, V var3) {
        this.underlyingIntegerMap.put(var3, id);
        this.putObject(var2, var3);
    }

    public void set(Map<ResourceLocation, Integer> resourceLocationIntegerMap) {
        Map<ResourceLocation, V> sorted = new HashMap<>();
        Map<ResourceLocation, V> unSorted = new HashMap<>();
        for (ResourceLocation key : this.registryObjects.keySet()) {
            if (!resourceLocationIntegerMap.containsKey(key)) {
                unSorted.put(key, this.registryObjects.get(key));
            } else {
                sorted.put(key, this.registryObjects.get(key));
            }
        }
        this.registryObjects.clear();
        this.underlyingIntegerMap.clear();
        resourceLocationIntegerMap.forEach((k, v) -> set(v, k, sorted.get(k)));
        unSorted.forEach(this::register);
    }

    public void validateKey() {
        Validate.notNull(this.defaultValue, "Missing default of DefaultedMappedRegistry: " + this.defaultValueKey, new Object[0]);
    }

    @Override
    protected Map<ResourceLocation, V> createUnderlyingMap() {
        return HashBiMap.create();
    }

    @Override
    @Nullable
    public V getObject(@Nullable ResourceLocation var1) {
        V var2 = super.getObject(var1);
        return var2 == null ? this.defaultValue : var2;
    }

    @Nullable
    public ResourceLocation getNameForObject(V var1) {
        ResourceLocation var2 = this.inverseObjectRegistry.get(var1);
        return var2 == null ? this.defaultValueKey : var2;
    }

    @Override
    public boolean containsKey(ResourceLocation var1) {
        return super.containsKey(var1);
    }

    public int getIDForObject(V var1) {
        int var2 = this.underlyingIntegerMap.getId(var1);
        return var2 == -1 ? this.underlyingIntegerMap.getId(this.defaultValue) : var2;
    }

    @Nullable
    public V getObjectById(int var1) {
        V var2 = this.underlyingIntegerMap.get(var1);
        return var2 == null ? this.defaultValue : var2;
    }

    @Override
    public Iterator<V> iterator() {
        return this.underlyingIntegerMap.iterator();
    }
}
