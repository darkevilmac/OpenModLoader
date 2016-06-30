package xyz.openmodloader.gui;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<String, Object> data = new HashMap<>();

    public Context set(String id, Object value) {
        if (data.containsKey(id)) throw new IllegalArgumentException(String.format("Key %s already has value %s", id, data.get(id)));
        data.put(id, value);
        return this;
    }

    public <T> T get(String id) {
        return (T)data.get(id);
    }

}
