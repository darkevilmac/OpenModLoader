package xyz.openmodloader.launcher;

import java.io.File;
import java.net.Proxy;

import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.launcher.strippable.Side;

public class OpenModLoaderClient {
    public static void main(String[] args) {
        File file = new File(".gradle/minecraft/natives/");
        if (!file.exists()) {
            file = new File("../.gradle/minecraft/natives/");
        }
        System.setProperty("org.lwjgl.librarypath", file.getAbsolutePath());
        OMLStrippableTransformer.SIDE = Side.CLIENT;
        LaunchArguments arguments = new LaunchArguments(args);

        if (arguments.containsArgument("password")) {
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1")).createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(arguments.getArgument("username"));
            auth.setPassword(arguments.getArgument("password"));
            arguments.removeArgument("password");

            try {
                auth.logIn();
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return;
            }

            arguments.addArgument("accessToken", auth.getAuthenticatedToken());
            arguments.addArgument("uuid", auth.getSelectedProfile().getId().toString().replace("-", ""));
            arguments.addArgument("username", auth.getSelectedProfile().getName());
            arguments.addArgument("userType", auth.getUserType().getName());
            arguments.addArgument("userProperties", new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create().toJson(auth.getUserProperties()));
        }

        arguments.addArgument("version", "1.10.2");
        arguments.addArgument("assetIndex", "1.10");
        arguments.addArgument("tweakClass", "xyz.openmodloader.launcher.OMLTweaker");

        if (arguments.containsArgument("accessToken")) {
            arguments.addArgument("accessToken", "OpenModLoader");
        }

        Launch.main(arguments.getArguments());
    }
}
