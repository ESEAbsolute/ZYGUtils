package icu.eseabs0.zygutils.handlers;

import icu.eseabs0.zygutils.config.ZYGConfig;
import icu.eseabs0.zygutils.types.ElementType;
import icu.eseabs0.zygutils.types.ElementOperationType;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import icu.eseabs0.zygutils.utils.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;

public final class ElementOperationHandler {
    private static final int LEFT_CLICK_BUTTON = 0;
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ZYGUtils-ElementOpScheduler"));
    private static final AtomicBoolean LOCKED = new AtomicBoolean(false);
    private static volatile List<Integer> PENDING_SLOTS = null;
    private static volatile ScheduledFuture<?> TIMEOUT_FUTURE = null;
    private static final AtomicBoolean DECIDED = new AtomicBoolean(false);
    private static volatile Runnable ON_TIMEOUT = null;

    private ElementOperationHandler() {}
    private static final ElementOperationHandler INSTANCE = new ElementOperationHandler();
    public static ElementOperationHandler getInstance() {
        return INSTANCE;
    }
    public static void setOnTimeout(Runnable r) { ON_TIMEOUT = r; }

    public void addFive(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void addStack(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void storeFive(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void storeAll(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void addRefined(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void storeRefined(ElementOperationType operation, ElementType element) {
        performElementBankOperation(element.getRow() * 9 + operation.getColumn());
    }
    public void storeEverything(ElementOperationType operation, ElementType element) {
        performElementBankOperation(ElementType.CONTROL_BAR.getRow() * 9 + operation.getColumn());
    }
    public void query(ElementOperationType operation, ElementType element) {
        performElementBankOperation(ElementType.CONTROL_BAR.getRow() * 9 + operation.getColumn());
    }

    public void performElementBankOperations(List<Integer> slots) {
        if (ZYGConfig.INSTANCE.debugStorageTrigger) {
            LogUtils.sendMessage("Trigger Element Storage");
        }

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) {
            return;
        }

        if (LOCKED.get()) return;

        PENDING_SLOTS = slots;
        LOCKED.set(true);
        DECIDED.set(false);
        networkHandler.sendCommand("menu6");

        TIMEOUT_FUTURE = SCHEDULER.schedule(ElementOperationHandler::timeout, 5, TimeUnit.SECONDS);
    }

    private void performElementBankOperation(int slot) {
        performElementBankOperations(List.of(slot));
    }

    public static boolean unlock(GenericContainerScreenHandler screenHandler) {
        if (DECIDED.compareAndSet(false, true)) {
            LOCKED.set(false);
            ScheduledFuture<?> future = TIMEOUT_FUTURE;
            if (future != null) {
                TIMEOUT_FUTURE = null;
                future.cancel(false);
            }
            List<Integer> slots = PENDING_SLOTS;
            PENDING_SLOTS = null;
            if (slots != null && !slots.isEmpty()) {
                ElementOperationHandler instance = getInstance();
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    ClientPlayerEntity player = client.player;
                    ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                    if (player == null || networkHandler == null) {
                        return;
                    }
                    instance.performInventoryOperations(slots, player, networkHandler, screenHandler);
                });
            }
            return true;
        } else {
            return false;
        }
    }

    private static void timeout() {
        if (DECIDED.compareAndSet(false, true)) {
            LOCKED.set(false);
            TIMEOUT_FUTURE = null;
            PENDING_SLOTS = null;
            Runnable r = ON_TIMEOUT;
            if (r != null) {
                r.run();
            }
        }
    }

    private void performInventoryOperations(
            List<Integer> slots,
            @NotNull ClientPlayerEntity player,
            @NotNull ClientPlayNetworkHandler networkHandler,
            @NotNull ScreenHandler screenHandler) {
        
        for (int slot : slots) {
            ClickSlotC2SPacket clickSlotPacket = new ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.getRevision(),
                    slot, // calculated above
                    LEFT_CLICK_BUTTON,
                    SlotActionType.THROW,
                    screenHandler.getSlot(slot).getStack(),
                    new Int2ObjectOpenHashMap<>()
            );
            networkHandler.sendPacket(clickSlotPacket);
            screenHandler.nextRevision(); // IMPORTANT: from MultiSelectionTest change
        }

        networkHandler.sendPacket(new CloseHandledScreenC2SPacket(screenHandler.syncId));
        player.closeHandledScreen();
    }
}
