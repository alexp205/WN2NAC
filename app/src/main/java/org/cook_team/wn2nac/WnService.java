package org.cook_team.wn2nac;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;

public class WnService extends Service {

    private static final EventBus bus = EventBus.getDefault();
    private static WnService context;
    public static WnService context() { return context; }
    public WnService() { context = WnService.this; }

    @Override
    public void onCreate() {

        context = this;
        if (!bus.isRegistered(this)) bus.register(this);

        WnSettings.read();
        WnHistory.read();
        WnWindoo.init();
        WnWindoo.start();
        WnLocation.init();
        WnLocation.enable();
        bus.post(new WnNetwork.InitEvent());

        /*bus.post(new WnSettings.SetIDEvent());
        if (wn2nacSettings.UserID.equals("")) {
            bus.post(new WnSettings.SetIDEvent());
        }*/

        //setupNotifications();
        //showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        WnWindoo.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        WnWindoo.stop();
        bus.unregister(this);
        context = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static int currentStep = 0;

    /** MESSAGING **/

    public static class DebugEvent {
        final String message;
        public DebugEvent(String message) {
            this.message = message;
        }
    }
    public void onEventMainThread(DebugEvent event) {
        if (WnSettings.debugOn)
            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
    }

    public static class ToastEvent {
        final String message;
        public ToastEvent(String message) {
            this.message = message;
        }
    }
    public void onEventMainThread(ToastEvent event) {
        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show();
    }
    public static void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void onEvent(SubscriberExceptionEvent exceptionEvent) {
        if (WnSettings.debugOn)
            bus.post(new WnService.DebugEvent("Exception:\n" + exceptionEvent.throwable.getMessage() + "\n" + exceptionEvent.throwable.getStackTrace()));
    }

    /** MEASURING **/

    public void onEventMainThread(WnMeasurement.StartEvent event) {
        WnMeasurement.start();
    }

    public void onEventMainThread(WnMeasurement.AbandonEvent event) {
        WnMeasurement.abandon();
    }

    public void onEventMainThread(WnMeasurement.FinishEvent event) {
        WnMeasurement.finish();
    }

    /** HISTORY **/


    /** NETWORK **/

    public void onEventMainThread(WnNetwork.InitEvent event) {
        WnNetwork.init();
    }

    public void onEventAsync(final WnNetwork.SendMeasurementEvent event) {
        WnNetwork.send(event.measurement);
    }

    /** LOCATION**/

    public void onEventMainThread(WnLocation.EnableLocationEvent event) {
        WnLocation.enable();
    }

    public void onEventMainThread(WnLocation.DisableLocationEvent event) {
        WnLocation.disable();
    }

    public void onEventMainThread(WnLocation.FetchLocationEvent event) {
        WnLocation.fetch();
    }

    /** NOTIFICATION **/

    /*private static final int NOTIFICATION = 1;
    public static final String CLOSE_ACTION = "close";
    @Nullable
    private NotificationManager mNotificationManager = null;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);

    private void setupNotifications() { //called in onCreate()
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ActivityMain.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ActivityMain.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(CLOSE_ACTION),
                0);
        mNotificationBuilder
                .setSmallIcon(R.mipmap.ntuas)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "", pendingCloseIntent)
                .setOngoing(true);
    }

    private void showNotification() {
        mNotificationBuilder
                .setTicker("Windoo儀器觀測中，按X以關閉")
                .setContentText("Windoo儀器觀測中，按X以關閉");
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
        }
    }*/
}


