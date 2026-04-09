package icu.eseabs0.zygutils.registry;

import icu.eseabs0.zygutils.types.CashType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CashRegistry extends PanlingRegistryBase<CashType> {
    public static final CashRegistry INSTANCE = new CashRegistry();

    public static final Identifier COPPER_CASH = id("panling", "copper_cash");
    public static final Identifier GOLD_INGOT = id("panling", "gold_ingot");
    public static final Identifier SILVER_TICKET = id("panling", "silver_ticket");

    private CashRegistry() {
        // Initialization is done in init() to avoid class loading deadlocks
    }

    public void init() {
        if (!TYPE_TO_ID.isEmpty()) return; // Already initialized

        map(CashType.COPPER_CASH, COPPER_CASH);
        map(CashType.GOLD_INGOT, GOLD_INGOT);
        map(CashType.SILVER_TICKET, SILVER_TICKET);
    }

    public static Optional<CashType> parseCash(ItemStack stack) {
        return INSTANCE.parse(stack);
    }
}
