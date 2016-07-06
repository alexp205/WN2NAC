package org.cook_team.wn2nac;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;

public class WnService extends Service {

    private static final EventBus bus = EventBus.getDefault();
    private static WnService context; // Singleton instance
    public static WnService context() { return context; }
    public WnService() { context = WnService.this; }

    @Override
    public void onCreate() {
        context = this;
        if (!bus.isRegistered(this)) bus.register(this);

        WnSettings.read();
        WnHistory.readAll();
        bus.post(new WnObserver.StartEvent());
        bus.post(new WnLocation.GetLastKnownLocationEvent());
        WnNetwork.init();

        //TESTING
        //Toast.makeText(context, "Service created", Toast.LENGTH_SHORT).show();

        /*bus.post(new WnSettings.SetIDEvent());
        if (wn2nacSettings.UserID.equals("")) {
            bus.post(new WnSettings.SetIDEvent());
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new WnObserver.StartEvent());
        //TESTING
        //Toast.makeText(context, "Service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        bus.post(new WnObserver.StopEvent());
        //TESTING
        //Toast.makeText(context, "Service destroyed", Toast.LENGTH_SHORT).show();
        bus.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    /** MESSAGING **/
    public static class ToastEvent {
        final String message;
        public ToastEvent(String message) { this.message = message; }
    }
    public void onEventMainThread(ToastEvent event) {
        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
    }
    public static class DebugEvent {
        final String message;
        public DebugEvent(String message) { this.message = message; }
    }
    public void onEventMainThread(DebugEvent event) {
        //if (WnSettings.debugOn)
            //Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
    }
    public void onEvent(SubscriberExceptionEvent exceptionEvent) {
        bus.post(new WnService.DebugEvent(getResources().getString(R.string.wnservice) + exceptionEvent.throwable.getMessage() + "\n" + exceptionEvent.throwable.getStackTrace()));
    }
    public static class VibrateEvent {
        final int millis;
        public VibrateEvent(int millis) { this.millis = millis; }
    }
    public void onEventMainThread(VibrateEvent event) {
        ((Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE)).vibrate(event.millis);
    }

}


