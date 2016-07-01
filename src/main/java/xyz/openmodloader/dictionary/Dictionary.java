package xyz.openmodloader.dictionary;

import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import xyz.openmodloader.util.CollectionPredicate;

public class Dictionary<K, V> {

    private final Map<K, CollectionPredicate<V>> map = Maps.newConcurrentMap();
    private final Predicate<V> defaultValue = (v) -> false;

    /**
     * Registers a value.
     *
     * @param key the key
     * @param value the value
     */
    public void register(K key, Predicate<V> value) {
        CollectionPredicate<V> collection = map.get(key);
        if (collection != null) {
            collection = new CollectionPredicate<>(Sets.newConcurrentHashSet());
            map.put(key, collection);
        }
        collection.add(value);
    }

    /**
     * Gets the registered elements for the specified key.
     * This set is automatically updated when new elements
     * are registered. Cache this.
     *
     * @param key the key
     * @return the sets the
     */
    public Predicate<V> get(K key) {
        Predicate<V> value = map.get(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
}