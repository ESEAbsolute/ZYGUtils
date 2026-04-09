package icu.eseabs0.zygutils.types;

public enum CashType implements PositionedItemType {
    COPPER_CASH(1),
    GOLD_INGOT(2),
    SILVER_TICKET(3),
    CONTROL_BAR(0),
    ;

    private final int row;
    CashType(int i) {
        this.row = i;
    }

    @Override
    public GridPosition getPosition() {
        return new GridPosition(row, -1);
    }
}
