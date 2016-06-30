package xyz.openmodloader.test;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import xyz.openmodloader.gui.Context;

public class GUITest extends GuiContainer {

    private static final ResourceLocation BG = new ResourceLocation("omltest:textures/gui/test.png");

    private int x, y, z;

    public GUITest(Context context) {
        super(new ContainerTest(context));
        BlockPos pos = context.get("pos");
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawString(mc.fontRendererObj, "OML GUI Test", guiLeft + 10, guiTop + 10, 0xFFFFFFFF);
        drawString(mc.fontRendererObj, String.format("Pos: (%d, %d, %d)", x, y, z), guiLeft + 10, guiTop + 20, 0xFFFFFFFF);
    }

}
