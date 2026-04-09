package icu.eseabs0.zygutils.mixin;

import icu.eseabs0.zygutils.events.MouseScrollListener;
import icu.eseabs0.zygutils.eventsystem.EventManager;
import icu.eseabs0.zygutils.config.WheelConfig;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method = "onMouseScroll(JDD)V", cancellable = true)
    private void onOnMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (WheelConfig.getInstance().isConfigMode()) {
            ci.cancel();
        }
        MouseScrollListener.MouseScrollEvent event = new MouseScrollListener.MouseScrollEvent(vertical);
        EventManager.fire(event);
    }
}
