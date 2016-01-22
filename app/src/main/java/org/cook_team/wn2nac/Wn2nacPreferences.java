package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;

public class Wn2nacPreferences {

    /** IDENTIFICATION */
    public static boolean IDset = false;
    public static String ID = "";

    public static void write() {
        SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(
                Wn2nacService.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("ID_set", IDset);
        editor.putString("ID", ID);
        editor.putString("server_address", Wn2nacNetwork.server_address);
        editor.putInt("nextSeq", Wn2nacHistory.nextSeq);
        editor.commit();
    }

    public static void read() {
        SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(
                Wn2nacService.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        IDset = sharedPref.getBoolean("ID_set", false);
        ID = sharedPref.getString("ID", Wn2nacService.context.getResources().getString(R.string.display_id));
        Wn2nacNetwork.server_address = sharedPref.getString("server_address", Wn2nacService.context.getResources().getString(R.string.default_server_address));
        Wn2nacHistory.nextSeq = sharedPref.getInt("nextSeq", 0);
    }
}
