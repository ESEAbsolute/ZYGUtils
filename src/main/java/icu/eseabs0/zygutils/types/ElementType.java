package icu.eseabs0.zygutils.types;

public enum ElementType implements PositionedItemType {
    METAL(0),
    WOOD(1),
    WATER(2),
    FIRE(3),
    EARTH(4),
    CONTROL_BAR(5),

    REFINED_METAL(0),
    REFINED_WOOD(1),
    REFINED_WATER(2),
    REFINED_FIRE(3),
    REFINED_EARTH(4),
    ;

    private final int row;
    ElementType(int i) {
        this.row = i;
    }

    @Override
    public GridPosition getPosition() {
        return new GridPosition(row, -1);
    }
}
