package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacHistory {

    private static EventBus bus = EventBus.getDefault();

    public static List<WindooMeasurement> history = new ArrayList<>();
    public static int nextSeq = 1;

    public static class SaveEvent {
        public final WindooMeasurement measurement;
        public SaveEvent(WindooMeasurement measurement) {
            this.measurement = measurement;
        }
    }
    public static void save(WindooMeasurement measurement) {
        try {
            String filename = "org.cook_team.wn2nac.HISTORY." + Integer.toString(measurement.getSeq());
            SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(filename, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt("Seq", measurement.getSeq());
            editor.putString("ID", measurement.getNickname());
            editor.putString("StartedAt", WindooEvent.dateFormat.format(measurement.getCreatedAt()));
            editor.putString("FinishedAt", WindooEvent.dateFormat.format(measurement.getUpdatedAt()));
            if (measurement.getSentAt() != null)
                editor.putString("SentAt", WindooEvent.dateFormat.format(measurement.getSentAt()));
            editor.putString("Latitude", measurement.getLatitude().toString());
            editor.putString("Longitude", measurement.getLongitude().toString());
            editor.putString("Altitude", measurement.getAltitude().toString());
            editor.putString("Wind", measurement.getWind().toString());
            editor.putString("Temperature", measurement.getTemperature().toString());
            editor.putString("Humidity", measurement.getHumidity().toString());
            editor.putString("Pressure", measurement.getPressure().toString());
            editor.putString("Orientation", measurement.getOrientation().toString());

            editor.commit(); }
        catch (Exception e) {
            bus.post(new Wn2nacService.ToastEvent("測量記錄儲存失敗"));
        }
    }

    public static class RefreshEvent {}
    public static void read() {
        try {
            File prefsdir = new File(Wn2nacService.context.getApplicationInfo().dataDir,"shared_prefs");
            File files[] = prefsdir.listFiles();
            history = new ArrayList<>();
            if(files != null) for (File file : files) {
                if(file.getName().startsWith("org.cook_team.wn2nac.HISTORY.")) {
                    SharedPreferences sharedPref = Wn2nacService.context.getSharedPreferences(file.getName().replace(".xml", ""), Context.MODE_PRIVATE);

                    WindooMeasurement measurement = new WindooMeasurement();
                    measurement.setSeq(sharedPref.getInt("Seq", 1));
                    measurement.setNickname(sharedPref.getString("ID", "ID"));
                    measurement.setLatitude(Double.valueOf(sharedPref.getString("Latitude", "0")));
                    measurement.setLongitude(Double.valueOf(sharedPref.getString("Longitude", "0")));
                    measurement.setAltitude(Double.valueOf(sharedPref.getString("Altitude", "0")));
                    measurement.setWind(Double.valueOf(sharedPref.getString("Wind", "0")));
                    measurement.setTemperature(Double.valueOf(sharedPref.getString("Temperature", "0")));
                    measurement.setHumidity(Double.valueOf(sharedPref.getString("Humidity", "0")));
                    measurement.setPressure(Double.valueOf(sharedPref.getString("Pressure", "0")));
                    measurement.setOrientation(Float.valueOf(sharedPref.getString("Orientation", "0")));
                    String createdAt = sharedPref.getString("StartedAt", null);
                    if(!(createdAt == null)) measurement.setCreatedAt(WindooEvent.dateFormat.parse(createdAt));
                    String updatedAt = sharedPref.getString("FinishedAt", null);
                    if(!(updatedAt == null)) measurement.setUpdatedAt(WindooEvent.dateFormat.parse(updatedAt));
                    String sentAt = sharedPref.getString("SentAt", null);
                    if(!(sentAt == null)) measurement.setSentAt(WindooEvent.dateFormat.parse(sentAt));

                    history.add(0, measurement);
                }
            }
        } catch (Exception e) {
            bus.post(new Wn2nacService.ToastEvent("測量記錄讀取失敗"));
        }
    }
}