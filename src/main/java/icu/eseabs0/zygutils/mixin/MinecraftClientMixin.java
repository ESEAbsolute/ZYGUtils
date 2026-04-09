package icu.eseabs0.zygutils.mixin;

import icu.eseabs0.zygutils.config.ZYGConfig;
import icu.eseabs0.zygutils.handlers.CashOperationHandler;
import icu.eseabs0.zygutils.handlers.ElementOperationHandler;
import icu.eseabs0.zygutils.modules.CashManager;
import icu.eseabs0.zygutils.utils.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;

        if (screen instanceof GenericContainerScreen containerScreen) {
            int containerSize = containerScreen.getScreenHandler().slots.size();
            if (containerSize == 90) {
                if (containerScreen.getTitle().contains(Text.of("元素银行"))) {
                    if (ElementOperationHandler.unlock(containerScreen.getScreenHandler())) {
                        if (ZYGConfig.INSTANCE.debugGuiOpen) {
                            LogUtils.sendMessage("Triggered Element Bank Chest GUI");
                        }
                        ci.cancel();
                    } else {
                        if (ZYGConfig.INSTANCE.debugGuiOpen) {
                            LogUtils.sendMessage("Triggered Element Bank Chest GUI manually");
                        }
                    }
                }
            } else if (containerSize == 81) {
                if (containerScreen.getTitle().contains(Text.of("掌上钱庄"))) {
                    if (CashOperationHandler.unlock(containerScreen.getScreenHandler())) {
                        if (ZYGConfig.INSTANCE.debugGuiOpen) {
                            LogUtils.sendMessage("Triggered Bank Chest GUI");
                        }
                        ci.cancel();
                    } else {
                        CashManager.getInstance().setCooldown(60);
                        if (ZYGConfig.INSTANCE.debugGuiOpen) {
                            LogUtils.sendMessage("Triggered Bank Chest GUI manually");
                        }
                    }
                }
            } else if (ZYGConfig.INSTANCE.debugGuiOpen) {
                LogUtils.sendMessage("containerSize: %d".formatted(containerSize));
                LogUtils.sendMessage("%s".formatted(containerScreen.getTitle()));
            }
        }
    }
}
