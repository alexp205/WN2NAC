package org.cook_team.wn2nac;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;

public class Wn2nacException {

    private static EventBus bus = EventBus.getDefault();

    public Wn2nacException() {
        if (!bus.isRegistered(this)) bus.register(this);
    }

    public void onEvent(SubscriberExceptionEvent exceptionEvent) {
        //bus.post(new Wn2nacService.ToastEvent("程式發生錯誤\n" + exceptionEvent.throwable.getMessage()));
    }
}
