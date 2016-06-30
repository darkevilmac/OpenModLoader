package xyz.openmodloader.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

import java.util.HashMap;
import java.util.Map;

public class GUIHandler {

    @Strippable(side = Side.CLIENT)
    private Map<String, Class<? extends GuiScreen>> guis = new HashMap<>();
    private Map<String, Class<? extends Container>> containers = new HashMap<>();

    @Strippable(side = Side.CLIENT)
    public GuiScreen getGUI(String id, Context context) {
        try {
            return guis.get(id).getConstructor(Context.class).newInstance(context);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Container getContainer(String id, Context context) {
        try {
            return containers.get(id).getConstructor(Context.class).newInstance(context);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Strippable(side = Side.CLIENT)
    public GUIHandler registerGUI(String id, Class<? extends GuiScreen> clazz) {
        guis.put(id, clazz);
        return this;
    }

    public GUIHandler registerContainer(String id, Class<? extends Container> clazz) {
        containers.put(id, clazz);
        return this;
    }

}
