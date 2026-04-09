package icu.eseabs0.zygutils.types;

public interface PositionedItemType {
    GridPosition getPosition();

    default boolean hasRow() {
        return getPosition() != null && getPosition().row() >= 0;
    }

    default boolean hasColumn() {
        return getPosition() != null && getPosition().column() >= 0;
    }

    default int getRow() {
        return getPosition() == null ? -1 : getPosition().row();
    }

    default int getColumn() {
        return getPosition() == null ? -1 : getPosition().column();
    }
}
