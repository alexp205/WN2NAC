package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WnHistory {

    private static final EventBus bus = EventBus.getDefault();

    /** HISTORY **/
    public static List<WindooMeasurement> history = new ArrayList<>();
    public static int nextSeq = 1;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Add measurement **/
    public static void add(WindooMeasurement measurement) {
        measurement.setSeq(nextSeq++);
        history.add(0, measurement);
        save(measurement);
        WnSettings.save();
        WnNetwork.send(measurement);
        bus.post(new RefreshEvent());
    }

    /** Save measurement **/
    public static void save(WindooMeasurement measurement) {
        try {
            String filename = "org.cook_team.wn2nac.HISTORY." + Integer.toString(measurement.getSeq());
            SharedPreferences sharedPref = WnService.context().getSharedPreferences(filename, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt("Seq", measurement.getSeq());
            editor.putString("ID", measurement.getNickname());
            if (measurement.getCreatedAt() != null)
                editor.putString("StartedAt", dateFormat.format(measurement.getCreatedAt()));
            if (measurement.getUpdatedAt() != null)
                editor.putString("FinishedAt", dateFormat.format(measurement.getUpdatedAt()));
            if (measurement.getSentAt() != null)
                editor.putString("SentAt", dateFormat.format(measurement.getSentAt()));
            if (measurement.getLatitude() != null)
                editor.putString("Latitude", measurement.getLatitude().toString());
            if (measurement.getLongitude() != null)
                editor.putString("Longitude", measurement.getLongitude().toString());
            if (measurement.getAltitude() != null)
                editor.putString("Altitude", measurement.getAltitude().toString());
            editor.putString("Wind", measurement.getWind().toString());
            editor.putString("Temperature", measurement.getTemperature().toString());
            editor.putString("Humidity", measurement.getHumidity().toString());
            editor.putString("Pressure", measurement.getPressure().toString());
            if (measurement.getOrientation() != null)
                editor.putString("Orientation", measurement.getOrientation().toString());

            editor.commit(); }
        catch (Exception e) {
            WnService.toast("測量記錄儲存失敗\n" + e.getMessage() + " \n" + e.getStackTrace());
        }
    }

    public static void read() {
        try {
            File prefsdir = new File(WnService.context().getApplicationInfo().dataDir,"shared_prefs");
            File files[] = prefsdir.listFiles();
            history = new ArrayList<>();
            if(files != null) for (File file : files) {
                if(file.getName().startsWith("org.cook_team.wn2nac.HISTORY.")) {
                    SharedPreferences sharedPref = WnService.context().getSharedPreferences(file.getName().replace(".xml", ""), Context.MODE_PRIVATE);

                    WindooMeasurement measurement = new WindooMeasurement();
                    measurement.setSeq(sharedPref.getInt("Seq", 1));
                    measurement.setNickname(sharedPref.getString("ID", "ID"));
                    measurement.setLatitude(Double.valueOf(sharedPref.getString("Latitude", "-9999")));
                    measurement.setLongitude(Double.valueOf(sharedPref.getString("Longitude", "-9999")));
                    measurement.setAltitude(Double.valueOf(sharedPref.getString("Altitude", "-9999")));
                    measurement.setWind(Double.valueOf(sharedPref.getString("Wind", "-9999")));
                    measurement.setTemperature(Double.valueOf(sharedPref.getString("Temperature", "-9999")));
                    measurement.setHumidity(Double.valueOf(sharedPref.getString("Humidity", "-9999")));
                    measurement.setPressure(Double.valueOf(sharedPref.getString("Pressure", "-9999")));
                    measurement.setOrientation(Float.valueOf(sharedPref.getString("Orientation", "-9999")));
                    String createdAt = sharedPref.getString("StartedAt", null);
                        if(createdAt != null) measurement.setCreatedAt(dateFormat.parse(createdAt));
                    String updatedAt = sharedPref.getString("FinishedAt", null);
                        if(updatedAt != null) measurement.setUpdatedAt(dateFormat.parse(updatedAt));
                    String sentAt = sharedPref.getString("SentAt", null);
                        if(sentAt != null) measurement.setSentAt(dateFormat.parse(sentAt));

                    history.add(0, measurement);
                }
                bus.post(new RefreshEvent());
            }
        } catch (Exception e) {
            WnService.toast("測量記錄讀取失敗");
        }
    }

    public static class RefreshEvent {} // Notify UI
}