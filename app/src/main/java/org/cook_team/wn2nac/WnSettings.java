package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;

import de.greenrobot.event.EventBus;

public class WnSettings {

    private static final EventBus bus = EventBus.getDefault();

    /** SETTINGS */
    public static boolean debugOn = false;
    public static String UserID = "";
    public static String WindooID = "";

    /** Save SETTINGS **/
    public static void save() {
        SharedPreferences sharedPref = WnService.context().getSharedPreferences(WnService.context().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("debugOn", debugOn);
        editor.putString("ID", UserID);
        editor.putString("WindooID", WindooID);
        editor.putInt("nextSeq", WnHistory.nextSeq);
        editor.commit();
    }

    /** Read SETTINGS **/
    public static void read() {
        SharedPreferences sharedPref = WnService.context().getSharedPreferences(WnService.context().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        debugOn = sharedPref.getBoolean("debugOn", false);
        UserID = sharedPref.getString("ID", "");
        WindooID = sharedPref.getString("WindooID", "");
        WnHistory.nextSeq = sharedPref.getInt("nextSeq", 1);
    }

    public static class SetIDEvent {}
}
