package icu.eseabs0.zygutils;

import icu.eseabs0.zygutils.events.WorldTimeUpdateListener;
import icu.eseabs0.zygutils.eventsystem.EventManager;
import icu.eseabs0.zygutils.events.MouseScrollListener;
import icu.eseabs0.zygutils.events.UpdateListener;
import icu.eseabs0.zygutils.commands.ZYGCommand;
import icu.eseabs0.zygutils.config.WheelConfig;
import icu.eseabs0.zygutils.modules.CashManager;
import icu.eseabs0.zygutils.modules.ElementManager;
import icu.eseabs0.zygutils.modules.GlobalTick;
import icu.eseabs0.zygutils.config.ZYGConfig;
import icu.eseabs0.zygutils.registry.CashRegistry;
import icu.eseabs0.zygutils.registry.ElementRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public enum ZYGUtilsClient {
    INSTANCE;

    private EventManager eventManager;

    public void onInitialize() {
        ZYGConfig.load();
        ElementRegistry.INSTANCE.init();
        CashRegistry.INSTANCE.init();
        ZYGCommand.register();
        eventManager = new EventManager();

        KeyBinding toggleKey = new KeyBinding(
                "Toggle Storage",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "ZYGUtils");
        KeyBindingHelper.registerKeyBinding(toggleKey);

        WheelConfig wheelConfig = WheelConfig.getInstance();
        wheelConfig.setToggleKey(toggleKey);
        eventManager.add(MouseScrollListener.class, wheelConfig);
        eventManager.add(UpdateListener.class, wheelConfig);

        GlobalTick globalTick = GlobalTick.getInstance();
        eventManager.add(UpdateListener.class, globalTick);
        eventManager.add(WorldTimeUpdateListener.class, globalTick);

        ElementManager elementManager = ElementManager.getInstance();
        eventManager.add(UpdateListener.class, elementManager);
        
        CashManager cashManager = CashManager.getInstance();
        eventManager.add(UpdateListener.class, cashManager);
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
