package xyz.openmodloader.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {

    private static final Map<String, GUIHandler> handlers = new HashMap<>();

    public static void register(String id, GUIHandler handler) {
        handlers.put(id, handler);
    }

    public static GUIHandler get(String id) {
        return handlers.get(id);
    }

    public static void open(EntityPlayer player, String modID, String guiID, Context context) {
        if (player.worldObj.isRemote) {
            openClient(player, modID, guiID, context);
        } else {
            openServer(player, modID, guiID, context);
        }
    }

    private static void openClient(EntityPlayer player, String modID, String guiID, Context context) {
        Minecraft.getMinecraft().displayGuiScreen(handlers.get(modID).getGUI(guiID, context));
    }

    private static void openServer(EntityPlayer player, String modID, String guiID, Context context) {
        player.openContainer = handlers.get(modID).getContainer(guiID, context);
    }

}
