package org.cook_team.wn2nac;

/**
 * Created by En on 2016/2/19.
 */
public class WnNotification {
    // TODO
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
