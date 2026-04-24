package icu.eseabs0.zygutils.modules;

import icu.eseabs0.zygutils.config.ZYGConfig;
import icu.eseabs0.zygutils.events.UpdateListener;
import icu.eseabs0.zygutils.handlers.CashOperationHandler;
import icu.eseabs0.zygutils.registry.CashRegistry;
import icu.eseabs0.zygutils.types.CashType;
import icu.eseabs0.zygutils.utils.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static icu.eseabs0.zygutils.types.CashOperationType.*;
import static icu.eseabs0.zygutils.types.CashType.*;

public final class CashManager implements UpdateListener {
    private CashManager() {}
    private static final CashManager INSTANCE = new CashManager();
    public static CashManager getInstance() { return INSTANCE; }

    private int cooldownTicks = 0;
    private int silverTicketProtectTicks = 0;

    public void setCooldown(int seconds) {
        this.cooldownTicks = seconds * 20;
        if (ZYGConfig.INSTANCE.autoDisableSilverTicket) {
            this.silverTicketProtectTicks = seconds * 20;
        }
    }

    @Override
    public void onUpdate() {
        if (!ZYGConfig.INSTANCE.masterSwitch) {
            return;
        }

        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        if (silverTicketProtectTicks > 0) {
            silverTicketProtectTicks--;
            if (silverTicketProtectTicks == 0 && ZYGConfig.INSTANCE.debugElementDecision) {
                LogUtils.sendMessage("银票保护期已过");
            }
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.currentScreen != null) {
            return;
        }

        long masterTick = GlobalTick.getInstance().getMasterTick();
        if ((masterTick) % (20 * 4) != 0) return;

        if (cooldownTicks > 0) return;

        processAutoStorage(client.player);
    }

    public List<Integer> calculateActionSlots(ClientPlayerEntity player) {
        List<Integer> slots = new ArrayList<>();
        Map<CashType, Integer> counts = getCashCounts(player);
        ZYGConfig config = ZYGConfig.INSTANCE;

        if (config.storeCopperCash && counts.getOrDefault(CashType.COPPER_CASH, 0) > 0) {
            slots.add(COPPER_CASH.getPosition().row() * 9 + STORE_ALL.getColumn());
            if (ZYGConfig.INSTANCE.debugElementDecision) {
                LogUtils.sendMessage("发现铜钱，执行存入全部铜钱");
            }
        }
        if (config.storeGoldIngot && counts.getOrDefault(CashType.GOLD_INGOT, 0) > 0) {
            slots.add(GOLD_INGOT.getPosition().row() * 9 + STORE_ALL.getColumn());
            if (ZYGConfig.INSTANCE.debugElementDecision) {
                LogUtils.sendMessage("发现元宝，执行存入全部元宝");
            }
        }
        // 当开启了存入银票 且 (未开启取出保护 或 不在保护期内) 时，才执行存入
        if (config.storeSilverTicket && counts.getOrDefault(CashType.SILVER_TICKET, 0) > 0) {
            if (!config.autoDisableSilverTicket || silverTicketProtectTicks <= 0) {
                slots.add(SILVER_TICKET.getPosition().row() * 9 + STORE_ALL.getColumn());
                if (ZYGConfig.INSTANCE.debugElementDecision) {
                    LogUtils.sendMessage("发现银票，执行存入全部银票");
                }
            } else if (ZYGConfig.INSTANCE.debugElementDecision) {
                LogUtils.sendMessage("发现银票，但在取出保护期内，暂不存入");
            }
        }

        return slots;
    }

    private void processAutoStorage(ClientPlayerEntity player) {
        List<Integer> slots = calculateActionSlots(player);
        if (slots.isEmpty()) return;

        CashOperationHandler.getInstance().performCashBankOperations(slots);
    }

    public Map<CashType, Integer> getCashCounts(ClientPlayerEntity player) {
        Map<CashType, Integer> counts = new EnumMap<>(CashType.class);
        int size = player.getInventory().size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            Optional<CashType> typeOpt = CashRegistry.parseCash(stack);
            typeOpt.ifPresent(type -> counts.put(type, counts.getOrDefault(type, 0) + stack.getCount()));
        }
        return counts;
    }
}
