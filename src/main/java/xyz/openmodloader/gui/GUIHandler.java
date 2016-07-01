package xyz.openmodloader.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GUIHandler {

    @Strippable(side = Side.CLIENT)
    private Map<String, Function<GUIContext, GuiScreen>> guis = new HashMap<>();
    private Map<String, Function<GUIContext, Container>> containers = new HashMap<>();

    @Strippable(side = Side.CLIENT)
    public GuiScreen getGUI(String id, GUIContext context) {
        return guis.get(id).apply(context);
    }

    public Container getContainer(String id, GUIContext context) {
        return containers.get(id).apply(context);
    }

    @Strippable(side = Side.CLIENT)
    public GUIHandler registerGUI(String id, Function<GUIContext, GuiScreen> creator) {
        guis.put(id, creator);
        return this;
    }

    public GUIHandler registerContainer(String id, Function<GUIContext, Container> creator) {
        containers.put(id, creator);
        return this;
    }

}
