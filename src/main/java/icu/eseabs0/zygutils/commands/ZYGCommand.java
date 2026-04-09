package icu.eseabs0.zygutils.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import icu.eseabs0.zygutils.handlers.CashOperationHandler;
import icu.eseabs0.zygutils.modules.CashManager;
import icu.eseabs0.zygutils.types.CashOperationType;
import icu.eseabs0.zygutils.types.CashType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import java.util.ArrayList;
import java.util.List;

public class ZYGCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("zygutils").executes(context -> { return 0; }));
            
            dispatcher.register(ClientCommandManager.literal("est")
                .then(ClientCommandManager.argument("amount", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        int amount = IntegerArgumentType.getInteger(context, "amount");
                        extractSilverTickets(amount);
                        return 1;
                    })
                )
            );
        });
    }

    private static void extractSilverTickets(int amount) {
        CashManager.getInstance().setCooldown(60);
        
        List<Integer> slots = new ArrayList<>();
        int remaining = amount;
        
        int stacks = remaining / 64;
        for (int i = 0; i < stacks; i++) {
            slots.add(CashType.SILVER_TICKET.getRow() * 9 + CashOperationType.ADD_STACK.getColumn());
        }
        remaining %= 64;
        
        int tens = remaining / 10;
        for (int i = 0; i < tens; i++) {
            slots.add(CashType.SILVER_TICKET.getRow() * 9 + CashOperationType.ADD_TEN.getColumn());
        }
        remaining %= 10;
        
        for (int i = 0; i < remaining; i++) {
            slots.add(CashType.SILVER_TICKET.getRow() * 9 + CashOperationType.ADD_ONE.getColumn());
        }
        
        if (!slots.isEmpty()) {
            CashOperationHandler.getInstance().performCashBankOperations(slots);
        }
    }
}
