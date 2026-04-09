package icu.eseabs0.zygutils.types;

import icu.eseabs0.zygutils.handlers.ElementOperationHandler;
import org.apache.logging.log4j.util.TriConsumer;

public enum ElementOperationType {
    ADD_FIVE(0, ElementOperationHandler::addFive),
    ADD_STACK(1, ElementOperationHandler::addStack),
    STORE_FIVE(3, ElementOperationHandler::storeFive),
    STORE_ALL(4, ElementOperationHandler::storeAll),
    ADD_REFINED(6, ElementOperationHandler::addRefined),
    STORE_REFINED(8, ElementOperationHandler::storeRefined),
    STORE_EVERYTHING(4, ElementOperationHandler::storeEverything),
    QUERY(7, ElementOperationHandler::query);

    private final int col;
    private final TriConsumer<ElementOperationHandler, ElementOperationType, ElementType> handler;
    ElementOperationType(int i, TriConsumer<ElementOperationHandler, ElementOperationType, ElementType> handler) {
        this.col = i;
        this.handler = handler;
    }

    public int getColumn() {
        return col;
    }

    public void perform(ElementType element) {
        if (handler == null) {
            throw new IllegalStateException("Operation handler not registered");
        }
        handler.accept(ElementOperationHandler.getInstance(), this, element);
    }
}
