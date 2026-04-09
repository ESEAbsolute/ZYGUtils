package icu.eseabs0.zygutils.config;

import icu.eseabs0.zygutils.events.MouseScrollListener;
import icu.eseabs0.zygutils.events.UpdateListener;
import icu.eseabs0.zygutils.modules.ElementManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class WheelConfig implements MouseScrollListener, UpdateListener {
    private static final WheelConfig INSTANCE = new WheelConfig();
    public static WheelConfig getInstance() { return INSTANCE; }

    private boolean isConfigMode = false;
    private int selectedRow = 0;
    private int selectedCol = 0;
    private boolean toggleKeyPressed = false;
    private KeyBinding toggleKey;

    private WheelConfig() {}

    public void setToggleKey(KeyBinding key) {
        this.toggleKey = key;
    }

    @Override
    public void onMouseScroll(double amount) {
        if (!isConfigMode) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.sneakKey.isPressed()) {
            // SHIFT + Scroll: switch row
            if (amount > 0) {
                selectedRow--;
            } else if (amount < 0) {
                selectedRow++;
            }
        } else {
            // Scroll: switch col
            if (amount > 0) {
                selectedCol--;
            } else if (amount < 0) {
                selectedCol++;
            }
        }
        wrapSelection();
    }

    private void wrapSelection() {
        ZYGConfig config = ZYGConfig.INSTANCE;
        int maxRow = 2; // Bank config + Debug
        if (config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            maxRow = 4; // + Alchemist + Refined
        }
        if (selectedRow < 0) selectedRow = maxRow;
        if (selectedRow > maxRow) selectedRow = 0;

        boolean isBankRow = (config.elementStoreMode != ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 1) ||
                            (config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 3);

        int maxCol = 0;
        if (selectedRow == 0) {
            maxCol = 1; // Master switch, Store mode
        } else if (selectedRow == 1 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            maxCol = 4; // 5 elements
        } else if (selectedRow == 2 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            maxCol = 4; // 5 refined elements
        } else if (isBankRow) {
            maxCol = 3; // 4 bank settings
        } else if (selectedRow == maxRow) { // debug
            maxCol = 2; // 3 debug settings
        }

        if (selectedCol < 0) selectedCol = maxCol;
        if (selectedCol > maxCol) selectedCol = 0;
    }

    @Override
    public void onUpdate() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || toggleKey == null) return;

        boolean isTogglePressed = toggleKey.isPressed();
        if (isTogglePressed && !toggleKeyPressed) {
            toggleKeyPressed = true;
            if (client.options.sneakKey.isPressed()) {
                // SHIFT + Toggle
                isConfigMode = !isConfigMode;
                if (!isConfigMode) {
                    ZYGConfig.save();
                    client.player.sendMessage(Text.translatable("text.zygutils.config.saved"), true);
                } else {
                    selectedRow = 0;
                    selectedCol = 0;
                }
            } else if (isConfigMode) {
                // Toggle current selection
                toggleSelection();
            }
        } else if (!isTogglePressed) {
            toggleKeyPressed = false;
        }

        if (isConfigMode) {
            displayActionBar(client.player);
        }
    }

    private void toggleSelection() {
        ZYGConfig config = ZYGConfig.INSTANCE;
        boolean isBankRow = (config.elementStoreMode != ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 1) ||
                            (config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 3);

        if (selectedRow == 0) {
            if (selectedCol == 0) {
                config.masterSwitch = !config.masterSwitch;
                if (config.masterSwitch) {
                    icu.eseabs0.zygutils.ZYGUtilsClient.INSTANCE.getEventManager().add(UpdateListener.class, ElementManager.getInstance());
                } else {
                    icu.eseabs0.zygutils.ZYGUtilsClient.INSTANCE.getEventManager().remove(UpdateListener.class, ElementManager.getInstance());
                }
            } else {
                int modeOrd = config.elementStoreMode.ordinal();
                config.elementStoreMode = ZYGConfig.ElementStoreMode.values()[(modeOrd + 1) % 3];
                wrapSelection(); // in case mode changed to/from alchemist
            }
        } else if (selectedRow == 1 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            toggleNormalStrategy(selectedCol);
        } else if (selectedRow == 2 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            toggleRefinedStrategy(selectedCol);
        } else if (isBankRow) {
            switch (selectedCol) {
                case 0 -> config.storeCopperCash = !config.storeCopperCash;
                case 1 -> config.storeGoldIngot = !config.storeGoldIngot;
                case 2 -> config.storeSilverTicket = !config.storeSilverTicket;
                case 3 -> config.autoDisableSilverTicket = !config.autoDisableSilverTicket;
            }
        } else {
            // Debug row
            switch (selectedCol) {
                case 0 -> config.debugGuiOpen = !config.debugGuiOpen;
                case 1 -> config.debugStorageTrigger = !config.debugStorageTrigger;
                case 2 -> config.debugElementDecision = !config.debugElementDecision;
            }
        }
    }

    private void toggleNormalStrategy(int col) {
        ZYGConfig config = ZYGConfig.INSTANCE;
        switch (col) {
            case 0 -> config.metalStrategy = nextNormal(config.metalStrategy);
            case 1 -> config.woodStrategy = nextNormal(config.woodStrategy);
            case 2 -> config.waterStrategy = nextNormal(config.waterStrategy);
            case 3 -> config.fireStrategy = nextNormal(config.fireStrategy);
            case 4 -> config.earthStrategy = nextNormal(config.earthStrategy);
        }
    }

    private void toggleRefinedStrategy(int col) {
        ZYGConfig config = ZYGConfig.INSTANCE;
        switch (col) {
            case 0 -> config.refinedMetalStrategy = nextRefined(config.refinedMetalStrategy);
            case 1 -> config.refinedWoodStrategy = nextRefined(config.refinedWoodStrategy);
            case 2 -> config.refinedWaterStrategy = nextRefined(config.refinedWaterStrategy);
            case 3 -> config.refinedFireStrategy = nextRefined(config.refinedFireStrategy);
            case 4 -> config.refinedEarthStrategy = nextRefined(config.refinedEarthStrategy);
        }
    }

    private ZYGConfig.NormalElementStrategy nextNormal(ZYGConfig.NormalElementStrategy s) {
        return ZYGConfig.NormalElementStrategy.values()[(s.ordinal() + 1) % 3];
    }

    private ZYGConfig.RefinedElementStrategy nextRefined(ZYGConfig.RefinedElementStrategy s) {
        return ZYGConfig.RefinedElementStrategy.values()[(s.ordinal() + 1) % 3];
    }

    private void displayActionBar(ClientPlayerEntity player) {
        ZYGConfig config = ZYGConfig.INSTANCE;
        boolean isBankRow = (config.elementStoreMode != ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 1) ||
                            (config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST && selectedRow == 3);

        String msg = "";
        if (selectedRow == 0) {
            msg = Text.translatable("text.zygutils.wheel.element").getString() + " | " +
                    (selectedCol == 0 ? "§e" : "") + Text.translatable("text.zygutils.wheel.enabled").getString() + (config.masterSwitch ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 1 ? "§e" : "") + Text.translatable("text.zygutils.wheel.mode").getString() + getModeName(config.elementStoreMode) + "§r";
        } else if (selectedRow == 1 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            msg = Text.translatable("text.zygutils.wheel.alchemist").getString() + " | " +
                    (selectedCol == 0 ? "§e" : "") + Text.translatable("text.zygutils.wheel.metal").getString() + getNormalName(config.metalStrategy) + "§r | " +
                    (selectedCol == 1 ? "§e" : "") + Text.translatable("text.zygutils.wheel.wood").getString() + getNormalName(config.woodStrategy) + "§r | " +
                    (selectedCol == 2 ? "§e" : "") + Text.translatable("text.zygutils.wheel.water").getString() + getNormalName(config.waterStrategy) + "§r | " +
                    (selectedCol == 3 ? "§e" : "") + Text.translatable("text.zygutils.wheel.fire").getString() + getNormalName(config.fireStrategy) + "§r | " +
                    (selectedCol == 4 ? "§e" : "") + Text.translatable("text.zygutils.wheel.earth").getString() + getNormalName(config.earthStrategy) + "§r";
        } else if (selectedRow == 2 && config.elementStoreMode == ZYGConfig.ElementStoreMode.ALCHEMIST) {
            msg = Text.translatable("text.zygutils.wheel.refined").getString() + " | " +
                    (selectedCol == 0 ? "§e" : "") + Text.translatable("text.zygutils.wheel.metal").getString() + getRefinedName(config.refinedMetalStrategy) + "§r | " +
                    (selectedCol == 1 ? "§e" : "") + Text.translatable("text.zygutils.wheel.wood").getString() + getRefinedName(config.refinedWoodStrategy) + "§r | " +
                    (selectedCol == 2 ? "§e" : "") + Text.translatable("text.zygutils.wheel.water").getString() + getRefinedName(config.refinedWaterStrategy) + "§r | " +
                    (selectedCol == 3 ? "§e" : "") + Text.translatable("text.zygutils.wheel.fire").getString() + getRefinedName(config.refinedFireStrategy) + "§r | " +
                    (selectedCol == 4 ? "§e" : "") + Text.translatable("text.zygutils.wheel.earth").getString() + getRefinedName(config.refinedEarthStrategy) + "§r";
        } else if (isBankRow) {
            msg = Text.translatable("text.zygutils.wheel.bank").getString() + " | " +
                    (selectedCol == 0 ? "§e" : "") + Text.translatable("text.zygutils.wheel.copper").getString() + (config.storeCopperCash ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 1 ? "§e" : "") + Text.translatable("text.zygutils.wheel.gold").getString() + (config.storeGoldIngot ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 2 ? "§e" : "") + Text.translatable("text.zygutils.wheel.silver").getString() + (config.storeSilverTicket ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 3 ? "§e" : "") + Text.translatable("text.zygutils.wheel.protect").getString() + (config.autoDisableSilverTicket ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r";
        } else {
            msg = Text.translatable("config.zygutils.debug").getString() + " | " +
                    (selectedCol == 0 ? "§e" : "") + Text.translatable("config.zygutils.debug_gui").getString() + ": " + (config.debugGuiOpen ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 1 ? "§e" : "") + Text.translatable("config.zygutils.debug_trigger").getString() + ": " + (config.debugStorageTrigger ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r | " +
                    (selectedCol == 2 ? "§e" : "") + Text.translatable("config.zygutils.debug_element_decision").getString() + ": " + (config.debugElementDecision ? Text.translatable("text.zygutils.wheel.on").getString() : Text.translatable("text.zygutils.wheel.off").getString()) + "§r";
        }
        player.sendMessage(Text.literal(msg), true);
    }

    private String getModeName(ZYGConfig.ElementStoreMode m) {
        return switch (m) {
            case STORE -> Text.translatable("config.zygutils.mode.store_all").getString();
            case IGNORE -> Text.translatable("config.zygutils.mode.ignore").getString();
            case ALCHEMIST -> Text.translatable("config.zygutils.mode.alchemist").getString();
        };
    }

    private String getNormalName(ZYGConfig.NormalElementStrategy s) {
        return switch (s) {
            case KEEP_10_15 -> Text.translatable("config.zygutils.strategy.keep_10_15").getString();
            case STORE_ALL -> Text.translatable("config.zygutils.strategy.store_all").getString();
            case IGNORE -> Text.translatable("config.zygutils.strategy.ignore_short").getString();
        };
    }

    private String getRefinedName(ZYGConfig.RefinedElementStrategy s) {
        return switch (s) {
            case KEEP_ABOVE_10 -> Text.translatable("config.zygutils.strategy.keep_above_10").getString();
            case STORE_ALL -> Text.translatable("config.zygutils.strategy.store_all").getString();
            case IGNORE -> Text.translatable("config.zygutils.strategy.ignore_short").getString();
        };
    }

    public boolean isConfigMode() {
        return isConfigMode;
    }
}