package xyz.openmodloader.dictionary;

import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import xyz.openmodloader.util.CollectionPredicate;

public class Dictionary<K, V> {

    private final Map<K, CollectionPredicate<V>> map = Maps.newConcurrentMap();

    /**
     * Registers a value.
     *
     * @param key the key
     * @param value the value
     */
    public void register(K key, Predicate<V> value) {
        getOrCreate(key).add(value);
    }

    /**
     * Gets the registered predicate for the specified key. The predicate is
     * automatically updated when a new element is registered. Cache this.
     *
     * @param key the key
     * @return the predicate
     */
    public Predicate<V> get(K key) {
        return getOrCreate(key);
    }

    private CollectionPredicate<V> getOrCreate(K key) {
        CollectionPredicate<V> value = map.get(key);
        if (value == null) {
            value = new CollectionPredicate<>(Sets.newConcurrentHashSet());
            map.put(key, value);
        }
        return value;
    }
}