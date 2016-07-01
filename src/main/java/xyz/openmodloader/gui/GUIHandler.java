package xyz.openmodloader.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A GUI handler that opens GUIs/containers.
 */
public class GUIHandler {

    @Strippable(side = Side.CLIENT)
    private Map<String, Function<GUIContext, GuiScreen>> guis = new HashMap<>();
    private Map<String, Function<GUIContext, Container>> containers = new HashMap<>();

    /**
     * Creates a GUI for the given ID from the given context
     * @param id The ID of the GUI
     * @param context The context to pass to the GUI creator
     * @return The GUI
     */
    @Strippable(side = Side.CLIENT)
    public GuiScreen getGUI(String id, GUIContext context) {
        return guis.get(id).apply(context);
    }

    /**
     * Creates a container for the given ID from the given context
     * @param id The ID of the container
     * @param context The context to pass to the container creator
     * @return The container
     */
    public Container getContainer(String id, GUIContext context) {
        return containers.get(id).apply(context);
    }

    /**
     * Registers a GUI creator for the given ID
     * @param id The ID of the GUI
     * @param creator The function that creates the GUI
     * @return This GUI handler
     */
    @Strippable(side = Side.CLIENT)
    public GUIHandler registerGUI(String id, Function<GUIContext, GuiScreen> creator) {
        guis.put(id, creator);
        return this;
    }

    /**
     * Registers a container creator for the given ID
     * @param id The ID of the GUI
     * @param creator The function that creates the GUI
     * @return This GUI handler
     */
    public GUIHandler registerContainer(String id, Function<GUIContext, Container> creator) {
        containers.put(id, creator);
        return this;
    }

}
