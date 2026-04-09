package icu.eseabs0.zygutils.utils;

import icu.eseabs0.zygutils.config.ZYGConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class LogUtils {
    public static void sendMessage(String message) {
        if (!ZYGConfig.INSTANCE.debugElementDecision) return;
        
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.sendMessage(Text.literal("[ZYGUtils] " + message), false);
        }
    }
}
