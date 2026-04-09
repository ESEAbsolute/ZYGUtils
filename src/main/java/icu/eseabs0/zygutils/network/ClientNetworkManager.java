package icu.eseabs0.zygutils.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkManager {
    @SuppressWarnings("InstantiationOfUtilityClass")
    private static class ClientNetworkManagerHolder {
        private static final ClientNetworkManager INSTANCE = new ClientNetworkManager();
    }

    private ClientNetworkManager() {}

    public static ClientNetworkManager getInstance() {
        return ClientNetworkManagerHolder.INSTANCE;
    }

    public static void init() {
//        ClientPlayNetworking.registerGlobalReceiver(
//
//        );
    }
}
