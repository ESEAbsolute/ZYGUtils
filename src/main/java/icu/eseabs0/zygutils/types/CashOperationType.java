package icu.eseabs0.zygutils.types;

import icu.eseabs0.zygutils.handlers.CashOperationHandler;
import org.apache.logging.log4j.util.TriConsumer;

public enum CashOperationType {
    STORE_ALL(1, CashOperationHandler::storeAll),
    ADD_ONE(3, CashOperationHandler::addOne),
    ADD_TEN(5, CashOperationHandler::addTen),
    ADD_STACK(7, CashOperationHandler::addStack),
    QUERY(0, CashOperationHandler::query);

    private final int col;
    private final TriConsumer<CashOperationHandler, CashOperationType, CashType> handler;
    CashOperationType(int i, TriConsumer<CashOperationHandler, CashOperationType, CashType> handler) {
        this.col = i;
        this.handler = handler;
    }

    public int getColumn() {
        return col;
    }

    public void perform(CashType element) {
        if (handler == null) {
            throw new IllegalStateException("Operation handler not registered");
        }
        handler.accept(CashOperationHandler.getInstance(), this, element);
    }
}
