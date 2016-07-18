package xyz.openmodloader.test.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.impl.InputEvent;
import xyz.openmodloader.gui.GUIContext;
import xyz.openmodloader.gui.GUIHandler;
import xyz.openmodloader.gui.GUIManager;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.test.TestMod;

/**
 * @author shadowfacts
 */
public class TestGUIHandler implements TestMod {

    @Override
    public void onInitialize() {
        GUIHandler handler = new GUIHandler();
        handler.registerContainer("test", ContainerTest::new);
        if (OpenModLoader.getSidedHandler().getSide() == Side.CLIENT) {
            handler.registerGUI("test", GuiContainerTest::new);
        }
        GUIManager.register("omltest", handler);

        OpenModLoader.getEventBus().register(InputEvent.Keyboard.class, this::onKeyPressed);
    }

    private void onKeyPressed(InputEvent.Keyboard event) {
        if (event.getKey() == Keyboard.KEY_BACKSLASH) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            player.openGUI("omltest", "test", new GUIContext().set("pos", player.getPosition()).set("inv", player.inventory));
        }
    }

    public static class ContainerTest extends Container {

        public ContainerTest(GUIContext context) {
            InventoryPlayer inventory = context.get("inv");

            for(int i = 0; i < 3; ++i) {
                for(int j = 0; j < 9; ++j) {
                    this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for(int i = 0; i < 9; ++i) {
                this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

    }

    public static class GuiContainerTest extends GuiContainer {

        private static final ResourceLocation BG = new ResourceLocation("omltest:textures/gui/test.png");

        private BlockPos pos;

        public GuiContainerTest(GUIContext context) {
            super(new ContainerTest(context));
            this.pos = context.get("pos");
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float delta, int mouseX, int mouseY) {
            mc.getTextureManager().bindTexture(BG);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
            drawString(mc.fontRendererObj, "OML GUI Test", guiLeft + 10, guiTop + 10, 0xFFFFFF);
            drawString(mc.fontRendererObj, String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ()), guiLeft + 10, guiTop + 20, 0xFFFFFF);
        }

    }

}
