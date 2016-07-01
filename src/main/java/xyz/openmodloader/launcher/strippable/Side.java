package xyz.openmodloader.launcher.strippable;

import xyz.openmodloader.launcher.OMLStrippableTransformer;

public enum Side {

    CLIENT,
    SERVER,
    UNIVERSAL;

    public static boolean isClient() {
        return getCurrent() == CLIENT;
    }

    public static boolean isServer() {
        return getCurrent() == SERVER;
    }

    public static Side getCurrent() {
        return OMLStrippableTransformer.getSide();
    }
}