package xyz.openmodloader.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages GUI handlers
 */
public class GUIManager {

    private static final Map<String, GUIHandler> handlers = new HashMap<>();

    /**
     * Registers the GUI handler for the given owner
     * @param owner The GUI handler's owner
     * @param handler The GUI handler
     */
    public static void register(String owner, GUIHandler handler) {
        handlers.put(owner, handler);
    }

    /**
     * Retrieves the handler for the given owner
     * @param owner The owner of the handler
     * @return The GUI handler
     */
    public static GUIHandler get(String owner) {
        return handlers.get(owner);
    }

    /**
     * Opens a GUI for the player from the given owner, GUI ID, and context
     * @param player The player to open the GUI
     * @param owner The GUI handler owner
     * @param guiID The GUI ID
     * @param context The context
     */
    public static void open(EntityPlayer player, String owner, String guiID, GUIContext context) {
        if (player.worldObj.isRemote) {
            openClient(player, owner, guiID, context);
        } else {
            openServer(player, owner, guiID, context);
        }
    }

    @Strippable(side = Side.CLIENT)
    private static void openClient(EntityPlayer player, String owner, String guiID, GUIContext context) {
        Minecraft.getMinecraft().displayGuiScreen(handlers.get(owner).getGUI(guiID, context));
    }

    private static void openServer(EntityPlayer player, String modID, String owner, GUIContext context) {
        player.openContainer = handlers.get(modID).getContainer(owner, context);
    }

}
