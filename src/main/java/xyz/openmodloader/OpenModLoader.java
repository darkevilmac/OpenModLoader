package xyz.openmodloader;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import xyz.openmodloader.dictionary.Dictionaries;
import xyz.openmodloader.event.EventBus;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.modloader.ModLoader;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.modloader.version.Version;
import xyz.openmodloader.network.Channel;
import xyz.openmodloader.network.ChannelManager;
import xyz.openmodloader.network.DataTypes;
import xyz.openmodloader.registry.GameRegistry;

public final class OpenModLoader {
    private static Version mcversion = new Version("1.10.2");
    private static Version version = new Version("0.0.1-develop");
    private static Logger logger = LogManager.getFormatterLogger("OpenModLoader");
    private static EventBus eventBus = new EventBus();
    private static Channel channel;
    private static SidedHandler sidedHandler;

    private OpenModLoader() {
    }

    /**
     * Initializes the API and starts mod loading. Called from
     * {@link Minecraft#startGame()} and {@link DedicatedServer#startServer()}.
     *
     * @param sidedHandler the sided handler
     */
    public static void minecraftConstruction(SidedHandler sidedHandler) {
        OpenModLoader.sidedHandler = sidedHandler;
        getLogger().info("Loading OpenModLoader " + getVersion());
        getLogger().info("Running Minecraft %s on %s using Java %s", mcversion, SystemUtils.OS_NAME, SystemUtils.JAVA_VERSION);
        GameRegistry.init();
        Dictionaries.init();
        ModLoader.loadMods();
        UpdateManager.checkForUpdates();
        getSidedHandler().onInitialize();
        channel = ChannelManager.create("oml")
                .createPacket("snackbar")
                .with("component", DataTypes.TEXT_COMPONENT)
                .handle((context, packet) -> getSidedHandler().openSnackbar(packet.get("component", DataTypes.TEXT_COMPONENT)))
                .build();
    }

    /**
     * Gets the current Minecraft version.
     *
     * @return the Minecraft version
     */
    public static Version getMinecraftVersion() {
        return mcversion;
    }

    /**
     * Gets the current OML version.
     *
     * @return the OML version
     */
    public static Version getVersion() {
        return version;
    }

    /**
     * Gets the OML logger. Primarily intended for internal use. Mods should
     * call {@link LogManager#getLogger(String)} to get a properly named logger.
     *
     * @return the OML logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Gets the main event bus. All OML events are fired here.
     *
     * @return the event bus
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Gets the sided handler.
     *
     * @return the sided handler
     */
    public static SidedHandler getSidedHandler() {
        return sidedHandler;
    }

    /**
     * Gets the type of the current runtime environment.
     *
     * @return the environment
     */
    public static Environment getEnvironment() {
        return OMLStrippableTransformer.getEnvironment();
    }

    /**
     * Gets OMLs internal networking channel.
     *
     * @return the channel
     */
    public static Channel getChannel() {
        return channel;
    }
}
