package xyz.openmodloader.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Multimap;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.OMLAccessTransformer;

public class ModLoader {
    /**
     * A list of all loaded mods.
     */
    public static final List<ModContainer> MODS = new ArrayList<>();

    /**
     * A map of all loaded mods. Key is the mod class and value is the ModContainer.
     */
    private static final Map<IMod, ModContainer> MODS_MAP = new HashMap<>();

    /**
     * The running directory for the game.
     */
    private static final File RUN_DIRECTORY = new File(".");

    /**
     * The directory to load mods from.
     */
    private static final File MOD_DIRECTORY = new File(RUN_DIRECTORY, "mods");

    /**
     * Attempts to load all mods from the mods directory. While this is public,
     * it is intended for internal use only!
     */
    public static void loadMods() {
        try {
            if (MOD_DIRECTORY.exists()) {
                File[] files = MOD_DIRECTORY.listFiles();
                if (files != null) {
                    for (File mod : files) {
                        Launch.classLoader.addURL(mod.toURI().toURL());
                    }
                }
            }

            URL roots;
            Enumeration<URL> metas = Launch.classLoader.getResources("META-INF");
            while (metas.hasMoreElements()) {
                roots = metas.nextElement();
                File root = new File(roots.getPath());
                File[] files = root.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().equals("MANIFEST.MF")) {
                            FileInputStream stream = new FileInputStream(file);
                            loadMod(file, new Manifest(stream));
                            stream.close();
                        } else if (file.getName().endsWith(".at")) {
                            Multimap<String, String> entries = OMLAccessTransformer.getEntries();
                            FileUtils.readLines(file).stream().filter(line -> line.matches("\\w+((\\.\\w+)+|)\\s+(\\w+(\\(\\S+|)|\\*\\(\\)|\\*)")).forEach(line -> {
                                String[] parts = line.split(" ");
                                entries.put(parts[0], parts[1]);
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to load a mod from an input stream. This will parse the
     * mods.json file and add the mod to {@link #MODS}.
     *
     * @param manifest the manifest instance
     */
    private static void loadMod(File file, Manifest manifest) {
        ModContainer container = ModContainer.create(manifest);
        if (container == null) {
            OpenModLoader.INSTANCE.getLogger().error("Found invalid manifest in file " + file.getAbsolutePath().replace("!", "").replace(File.separator + "META-INF" + File.separator + "MANIFEST.MF", ""));
            return;
        }
        OpenModLoader.INSTANCE.getLogger().info("Found mod " + container.getName() + " with id " + container.getModID());
        MODS.add(container);
        if (container.getTransformer() != null) {
            Launch.classLoader.registerTransformer(container.getTransformer());
        }
    }

    /**
     * Iterates through all registered mods and enables them. If there is an
     * issue in registering the mod, it will be disabled.
     */
    public static void registerMods() {
        for (ModContainer mod : MODS) {
            try {
                IMod instance = mod.getInstance();
                MODS_MAP.put(instance, mod);
                instance.onEnable();
            } catch (RuntimeException e) {
                OpenModLoader.INSTANCE.getLogger().warn("An error occurred while enabling mod " + mod.getModID());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the mod container of a mod.
     *
     * @param mod the mod instance
     * @return the mod container
     */
    public static ModContainer getContainer(IMod mod) {
        return MODS_MAP.get(mod);
    }
}
