package icu.eseabs0.zygutils.modules;

import icu.eseabs0.zygutils.config.ZYGConfig;
import icu.eseabs0.zygutils.config.ZYGConfig.*;
import icu.eseabs0.zygutils.events.UpdateListener;
import icu.eseabs0.zygutils.handlers.ElementOperationHandler;
import icu.eseabs0.zygutils.types.ElementType;
import icu.eseabs0.zygutils.registry.ElementRegistry;
import icu.eseabs0.zygutils.utils.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static icu.eseabs0.zygutils.types.ElementType.*;
import static icu.eseabs0.zygutils.types.ElementOperationType.*;

public final class ElementManager implements UpdateListener {
    private ElementManager() {}
    private static final ElementManager INSTANCE = new ElementManager();
    public static ElementManager getInstance() { return INSTANCE; }

    @Override
    public void onUpdate() {
        if (!ZYGConfig.INSTANCE.masterSwitch) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.currentScreen instanceof HandledScreen ||
                client.currentScreen instanceof ChatScreen) {
            return;
        } else {
            ClientPlayerEntity player = client.player;
            if (client.currentScreen != null) {
                player.sendMessage(Text.of(client.currentScreen.getClass().getName()));
                player.sendMessage(Text.of(client.currentScreen.getClass().getTypeName()));
            } else {
                player.sendMessage(Text.of("null screen"));
            }
        }

        long masterTick = GlobalTick.getInstance().getMasterTick();
        if ((masterTick - 40) % (20 * 4) != 0) return;

        processAutoStorage(client.player);
    }

    public List<Integer> calculateActionSlots(ClientPlayerEntity player) {
        List<Integer> slots = new ArrayList<>();
        Map<ElementType, Integer> counts = getElementCounts(player);
        ZYGConfig config = ZYGConfig.INSTANCE;
        ElementStoreMode mode = config.elementStoreMode;

        if (mode == ElementStoreMode.IGNORE) return slots;

        if (mode == ElementStoreMode.STORE) {
            boolean hasNormalElementToStore = false;
            for (Map.Entry<ElementType, Integer> entry : counts.entrySet()) {
                ElementType type = entry.getKey();
                int count = entry.getValue();
                if (count > 0 && !isRefined(type) && type != CONTROL_BAR) {
                    hasNormalElementToStore = true;
                    break;
                }
            }
            if (hasNormalElementToStore) {
                slots.add(CONTROL_BAR.getRow() * 9 + STORE_EVERYTHING.getColumn());
                if (ZYGConfig.INSTANCE.debugElementDecision) {
                    LogUtils.sendMessage("发现普通元素，执行全部存入");
                }
            }
        } else if (mode == ElementStoreMode.ALCHEMIST) {
            for (ElementType type : ElementType.values()) {
                if (type == CONTROL_BAR || type.getRow() < 0) continue;
                int count = counts.getOrDefault(type, 0);

                if (isRefined(type)) {
                    RefinedElementStrategy strategy = getRefinedStrategy(type, config);
                    if (strategy == RefinedElementStrategy.STORE_ALL && count > 0) {
                        slots.add(type.getRow() * 9 + STORE_REFINED.getColumn());
                        if (ZYGConfig.INSTANCE.debugElementDecision) {
                            LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行操作 STORE_REFINED x1");
                        }
                    } else if (strategy == RefinedElementStrategy.KEEP_ABOVE_10) {
                        if (count < 10) {
                            slots.add(type.getRow() * 9 + ADD_REFINED.getColumn());
                            if (ZYGConfig.INSTANCE.debugElementDecision) {
                                LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行操作 ADD_REFINED x1");
                            }
                        } else {
                            if (ZYGConfig.INSTANCE.debugElementDecision) {
                                LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；不执行操作");
                            }
                        }
                    } else {
                        if (ZYGConfig.INSTANCE.debugElementDecision) {
                            LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；策略为IGNORE，不执行操作");
                        }
                    }
                } else {
                    NormalElementStrategy strategy = getNormalStrategy(type, config);
                    if (strategy == NormalElementStrategy.STORE_ALL && count > 0) {
                        slots.add(type.getRow() * 9 + STORE_ALL.getColumn());
                        if (ZYGConfig.INSTANCE.debugElementDecision) {
                            LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行操作 STORE_ALL x1");
                        }
                    } else if (strategy == NormalElementStrategy.KEEP_10_15) {
                        if (count > 15) {
                            if (count > 64 + 15) {
                                // 优化：数量极多时，直接全部存入，再取出 2 次 +5
                                slots.add(type.getRow() * 9 + STORE_ALL.getColumn());
                                slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                if (ZYGConfig.INSTANCE.debugElementDecision) {
                                    LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行优化操作：全部存入并取出 10 个");
                                }
                            } else {
                                int clicks = (int) Math.ceil((count - 15) / 5.0);
                                if (clicks > 5) {
                                    // 优化：如果需要存的数量过多，直接全部存入，再取出 2 次 +5
                                    slots.add(type.getRow() * 9 + STORE_ALL.getColumn());
                                    slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                    slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                    if (ZYGConfig.INSTANCE.debugElementDecision) {
                                        LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行优化操作：全部存入并取出 10 个");
                                    }
                                } else {
                                    for (int i = 0; i < clicks; i++) {
                                        slots.add(type.getRow() * 9 + STORE_FIVE.getColumn());
                                    }
                                    if (ZYGConfig.INSTANCE.debugElementDecision) {
                                        LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行操作 STORE_FIVE x" + clicks);
                                    }
                                }
                            }
                        } else if (count < 10) {
                            int clicks = (int) Math.ceil((10 - count) / 5.0);
                            if (clicks > 5) {
                                // 优化：如果需要取的数量过多，直接全部存入，再取出 2 次 +5
                                slots.add(type.getRow() * 9 + STORE_ALL.getColumn());
                                slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                if (ZYGConfig.INSTANCE.debugElementDecision) {
                                    LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行优化操作：全部存入并取出 10 个");
                                }
                            } else {
                                for (int i = 0; i < clicks; i++) {
                                    slots.add(type.getRow() * 9 + ADD_FIVE.getColumn());
                                }
                                if (ZYGConfig.INSTANCE.debugElementDecision) {
                                    LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；执行操作 ADD_FIVE x" + clicks);
                                }
                            }
                        } else {
                            if (ZYGConfig.INSTANCE.debugElementDecision) {
                                LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；不执行操作");
                            }
                        }
                    } else {
                        if (ZYGConfig.INSTANCE.debugElementDecision) {
                            LogUtils.sendMessage("当前 " + type.name() + "：" + count + " 个；策略为IGNORE，不执行操作");
                        }
                    }
                }
            }
        }
        
        return slots;
    }

    private void processAutoStorage(ClientPlayerEntity player) {
        List<Integer> slots = calculateActionSlots(player);
        if (slots.isEmpty()) return;

        ElementOperationHandler.getInstance().performElementBankOperations(slots);
    }

    public Map<ElementType, Integer> getElementCounts(ClientPlayerEntity player) {
        Map<ElementType, Integer> counts = new EnumMap<>(ElementType.class);
        int size = player.getInventory().size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            Optional<ElementType> typeOpt = ElementRegistry.parseElement(stack);
            typeOpt.ifPresent(type -> counts.put(type, counts.getOrDefault(type, 0) + stack.getCount()));
        }
        return counts;
    }

    private NormalElementStrategy getNormalStrategy(ElementType type, ZYGConfig config) {
        return switch (type) {
            case METAL -> config.metalStrategy;
            case WOOD -> config.woodStrategy;
            case WATER -> config.waterStrategy;
            case FIRE -> config.fireStrategy;
            case EARTH -> config.earthStrategy;
            default -> NormalElementStrategy.IGNORE;
        };
    }

    private RefinedElementStrategy getRefinedStrategy(ElementType type, ZYGConfig config) {
        return switch (type) {
            case REFINED_METAL -> config.refinedMetalStrategy;
            case REFINED_WOOD -> config.refinedWoodStrategy;
            case REFINED_WATER -> config.refinedWaterStrategy;
            case REFINED_FIRE -> config.refinedFireStrategy;
            case REFINED_EARTH -> config.refinedEarthStrategy;
            default -> RefinedElementStrategy.IGNORE;
        };
    }

    private boolean isRefined(ElementType type) {
        return type == REFINED_METAL
                || type == REFINED_WOOD
                || type == REFINED_WATER
                || type == REFINED_FIRE
                || type == REFINED_EARTH;
    }
}
