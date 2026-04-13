package icu.eseabs0.zygutils.events;

import icu.eseabs0.zygutils.eventsystem.Event;

import java.util.ArrayList;
import java.util.EventListener;

public interface UpdateListener extends EventListener {
    void onUpdate();

    class UpdateEvent extends Event<UpdateListener> {
        @Override
        public void fire(ArrayList<UpdateListener> listeners) {
            for(UpdateListener listener : listeners) {
                listener.onUpdate();
            }
        }

        @Override
        public Class<UpdateListener> getListenerType() {
            return UpdateListener.class;
        }
    }
}
