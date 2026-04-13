package icu.eseabs0.zygutils;

import net.fabricmc.api.ClientModInitializer;

public class ZYGUtilsClientHolder implements ClientModInitializer {
    private static boolean initialized;

    @Override
    public void onInitializeClient() {
        if (initialized) throw new RuntimeException("onInitializeClient() ran twice!");
        ZYGUtilsClient.INSTANCE.onInitialize();
        initialized = true;
    }
}
