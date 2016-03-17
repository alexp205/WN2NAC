package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;

import de.greenrobot.event.EventBus;

public class WnSettings {

    private static final EventBus bus = EventBus.getDefault();

    /** SETTINGS */
    private static WindooUser windooUser = new WindooUser();
    private static int windooID = -1;

    /** DEVELOPMENT settings **/
    public static boolean debugOn = false;

    /** GETTERs **/
    public static WindooUser getWindooUser()    { return windooUser; }
    public static int getWindooUserID()         { return windooUser.getUserID(); }
    public static int getWindooID()             { return windooID; }

    /** SETTERs **/
    public static void setWindooUserID(int userID)  { windooUser.setUserID(userID); }
    public static void setWindooID(int id)          { windooID = id; }

    /** SAVE settings **/
    public static void save() {
        SharedPreferences sharedPref = WnService.context().getSharedPreferences(WnService.context().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ID", String.valueOf(windooUser.getUserID()));
        editor.putString("WindooID", String.valueOf(windooID));
        editor.putBoolean("debugOn", debugOn);
        editor.commit();
        bus.post(new WnService.ToastEvent("設定已儲存"));
    }

    /** READ settings **/
    public static void read() {
        SharedPreferences sharedPref = WnService.context().getSharedPreferences(WnService.context().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        try { windooUser.setUserID(Integer.valueOf(sharedPref.getString("ID", "0"))); } catch(Exception e) { windooUser.setUserID(0); }
        try { windooID = Integer.valueOf(sharedPref.getString("WindooID", "0")); } catch(Exception e) { windooID = 0; }
        debugOn = sharedPref.getBoolean("debugOn", false);
    }
}
