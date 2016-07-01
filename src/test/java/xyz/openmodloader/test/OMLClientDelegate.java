package xyz.openmodloader.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiModInfo;
import xyz.openmodloader.client.gui.GuiModList;
import xyz.openmodloader.event.impl.*;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.network.Channel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.util.text.TextComponentString;

import org.lwjgl.input.Keyboard;

/**
 * The client delegate of the test mod, this will only run on the client
 */
public class OMLClientDelegate extends OMLServerDelegate {

    @Override
    public void registerEvents(Channel channel) {
        super.registerEvents(channel);

        System.out.println("Client");

        OpenModLoader.getEventBus().register(GuiEvent.Open.class, this::onGuiOpen);
        OpenModLoader.getEventBus().register(GuiEvent.Draw.class, this::onGuiDraw);
        OpenModLoader.getEventBus().register(GuiEvent.SplashLoad.class, this::onSplashLoad);

        OpenModLoader.getEventBus().register(InputEvent.Keyboard.class, this::onKeyPressed);
        OpenModLoader.getEventBus().register(InputEvent.Mouse.class, this::onMouseClick);

        OpenModLoader.getEventBus().register(ScreenshotEvent.class, this::onScreenshot);
        OpenModLoader.getEventBus().register(MessageEvent.Chat.class, event -> {
            if (event.getSide() == Side.CLIENT) {
                String message = event.getMessage().getUnformattedText();
                if (message.equals(I18n.format("tile.bed.occupied")) ||
                        message.equals(I18n.format("tile.bed.noSleep")) ||
                        message.equals(I18n.format("tile.bed.notSafe")) ||
                        message.equals(I18n.format("tile.bed.notValid"))) {
                    OpenModLoader.getSidedHandler().openSnackbar(event.getMessage());
                    event.setCanceled(true);
                }
            }
        });

        OpenModLoader.getEventBus().register(BiomeEvent.BiomeColor.Grass.class, this::onGrassColor);
        OpenModLoader.getEventBus().register(BiomeEvent.BiomeColor.Foliage.class, this::onFoliageColor);
        OpenModLoader.getEventBus().register(BiomeEvent.BiomeColor.Water.class, this::onWaterColor);
    }

    private void onGuiOpen(GuiEvent.Open event) {
        OpenModLoader.getLogger().info("Opening gui: " + event.getGui());
        if (event.getGui() instanceof GuiLanguage) {
            event.setCanceled(true);
        } else if (event.getGui() instanceof GuiMainMenu) {
            if (!UpdateManager.getOutdatedMods().isEmpty()) {
                OpenModLoader.getSidedHandler().openSnackbar(new TextComponentString("Mod updates found!"));
            }
        }
    }

    private void onGuiDraw(GuiEvent.Draw event) {
        if (!(event.getGui() instanceof GuiMainMenu) && !(event.getGui() instanceof GuiModList) && !(event.getGui() instanceof GuiModInfo)) {
            Minecraft.getMinecraft().fontRendererObj.drawString("Open Mod Loader", 5, 5, 0xFFFFFFFF);
        }
    }

    private void onSplashLoad(GuiEvent.SplashLoad event) {
        event.getSplashTexts().clear();
        event.getSplashTexts().add("OpenModLoader Test!");
    }

    private void onKeyPressed(InputEvent.Keyboard event) {
        OpenModLoader.getLogger().info(String.format("Key pressed %c (%d)", event.getCharacter(), event.getKey()));

        if (event.getKey() == Keyboard.KEY_SEMICOLON) {
            channel.send("test")
                    .set("str", "Hello, Server!")
                    .toServer();
        }
    }

    private void onMouseClick(InputEvent.Mouse event) {
        OpenModLoader.getLogger().info(String.format("Mouse clicked, %d", event.getButton()));
        if (event.getButton() == Keyboard.KEY_S) {
            event.setCanceled(true);
        }
    }

    private void onScreenshot(ScreenshotEvent event) {
        event.setScreenshotFile(new File("screenshotevent/", event.getScreenshotFile().getName()));
        event.setResultMessage(new TextComponentString("Screenshot saved to " + event.getScreenshotFile().getPath()));
        final BufferedImage image = event.getImage();
        final Graphics graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString("Open Mod Loader", 20, 40);
    }

    private void onGrassColor(BiomeEvent.BiomeColor.Grass event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }

    private void onFoliageColor(BiomeEvent.BiomeColor.Foliage event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }

    private void onWaterColor(BiomeEvent.BiomeColor.Water event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }
}
