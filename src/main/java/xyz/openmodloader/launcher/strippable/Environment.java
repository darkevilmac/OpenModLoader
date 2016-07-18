package xyz.openmodloader.launcher.strippable;

import xyz.openmodloader.launcher.OMLStrippableTransformer;

public enum Environment {

    PRODUCTION, DEVELOPMENT, UNIVERSAL;

    public static boolean isDevelopment() {
        return getCurrent() == DEVELOPMENT;
    }

    public static boolean isProduction() {
        return getCurrent() == PRODUCTION;
    }

    public static Environment getCurrent() {
        return OMLStrippableTransformer.getEnvironment();
    }
}