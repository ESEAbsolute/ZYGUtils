package icu.eseabs0.zygutils.mixin;

import icu.eseabs0.zygutils.events.WorldTimeUpdateListener;
import icu.eseabs0.zygutils.eventsystem.EventManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        EventManager.fire(new WorldTimeUpdateListener.WorldTimeUpdateEvent(packet.getTime(), packet.getTimeOfDay()));
    }
}
