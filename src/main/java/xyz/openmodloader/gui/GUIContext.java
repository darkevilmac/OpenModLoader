package xyz.openmodloader.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * The context for opening a GUI/container.
 * Allows for storing arbitrary data.
 */
public class GUIContext {

    private final Map<String, Object> data = new HashMap<>();

    /**
     * Stores the given value with the given ID
     * @param id The ID to use as a key
     * @param value The value store
     * @return This context
     */
    public GUIContext set(String id, Object value) {
        if (data.containsKey(id)) throw new IllegalArgumentException(String.format("Key %s already has value %s", id, data.get(id)));
        data.put(id, value);
        return this;
    }

    /**
     * Retrieves a value from the given ID
     * @param id The ID of the thing to retrieve
     * @param <T> The type of the thing to retrieve
     * @return The value
     */
    public <T> T get(String id) {
        return (T)data.get(id);
    }

}
