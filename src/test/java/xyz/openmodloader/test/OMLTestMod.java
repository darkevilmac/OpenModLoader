package xyz.openmodloader.test;

import java.io.File;

import xyz.openmodloader.registry.Delegate;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.config.Config;
import xyz.openmodloader.modloader.Mod;
import xyz.openmodloader.network.Channel;
import xyz.openmodloader.network.ChannelManager;
import xyz.openmodloader.network.DataTypes;
import xyz.openmodloader.registry.GameRegistry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class OMLTestMod implements Mod {
    private Channel channel;

    @Delegate
    private OMLServerDelegate delegate;

    @Override
    public void onInitialize() {

        OpenModLoader.getLogger().info("Loading test mod");

        delegate.registerEvents(channel);

        Config config = new Config(new File("./config/test.conf"));
        Config category1 = config.getConfig("category1", "configures stuff");
        category1.getBoolean("boolean1", true, "this is a boolean");
        category1.getBoolean("boolean2", true, "this is another boolean");
        Config category2 = category1.getConfig("category2", "configures more stuff");
        category2.getString("string1", "string", "this is a string");
        config.save();

        testNetwork();

        testBlock();
    }

    private void testNetwork() {
        channel = ChannelManager.create("OMLTest")
                .createPacket("test")
                .with("str", DataTypes.STRING)
                .handle((context, packet) -> {
                    System.out.println("PHYSICAL SIDE: " + OpenModLoader.getSidedHandler().getSide());
                    System.out.println("THREAD: " + Thread.currentThread().getName());
                    System.out.println("DATA: " + packet.get("str", DataTypes.STRING));
                })
                .build();
    }

    private void testBlock() {
        GameRegistry.registerBlock(new BlockEmpty(), new ResourceLocation("oml:blank"));
        GameRegistry.registerBlock(new BlockTest(), new ResourceLocation("oml:test"));

        GameRegistry.registerItem(new Item(), new ResourceLocation("oml:blank_item"));
        GameRegistry.registerItem(new ItemTestHorseArmor(), new ResourceLocation("oml:horse_armor"));
    }
}
