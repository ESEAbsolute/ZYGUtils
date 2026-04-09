package icu.eseabs0.zygutils.events;

import icu.eseabs0.zygutils.eventsystem.Event;

import java.util.ArrayList;
import java.util.EventListener;

public interface MouseScrollListener extends EventListener {
    void onMouseScroll(double amount);
    class MouseScrollEvent extends Event <MouseScrollListener> {
        private final double amount;
        public MouseScrollEvent(double amount) { this.amount = amount; }
        @Override
        public void fire(ArrayList<MouseScrollListener> listeners) {
            for(MouseScrollListener listener : listeners) { listener.onMouseScroll(amount); }
        }
        @Override
        public Class<MouseScrollListener> getListenerType() { return MouseScrollListener.class; }
    }
}
