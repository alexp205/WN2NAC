package org.cook_team.wn2nac;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class WnHistory {

    private static final EventBus bus = EventBus.getDefault();

    /** FORMATS **/
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** EVENTS **/
    public static class DatasetChangedEvent {} // Notify UI

    /** HISTORY **/
    private static IndexedMap<String, WindooMeasurement> history = new IndexedMap<>();
    public static WindooMeasurement get(String id) { return history.get(id); }
    public static WindooMeasurement get(int index) { return history.get(index); }
    public static int size() { return history.size(); }
    public static IndexedMap<String, WindooMeasurement> getHistory() { return history; }

    /** Add single measurement  record **/
    public static void add(WindooMeasurement measurement) {
        history.put(measurement.getMeasurementID(), measurement);
        bus.post(new DatasetChangedEvent());
        saveVersion1(measurement);
    }

    /** Read all measurement records **/
    public static void readAll() {
        try {
            history.clear();
            File files[] = new File(WnService.context().getApplicationInfo().dataDir,"shared_prefs").listFiles();
            if(files != null) for (File file : files) {
                if(file.getName().startsWith("org.cook_team.wn2nac.HISTORY.")) {
                    readVersion1(file.getName());
                }
                bus.post(new DatasetChangedEvent());
            }
        } catch (Exception e) { bus.post(new WnService.ToastEvent("測量記錄讀取失敗")); }
    }

    public static void save(WindooMeasurement measurement) {
        save(measurement);
    }

    /** Save single measurement record **/
    public static void saveVersion1(WindooMeasurement measurement) {
        try {
            String filename = "org.cook_team.wn2nac.HISTORY." + measurement.getMeasurementID();
            SharedPreferences.Editor editor = WnService.context().getSharedPreferences(filename, Context.MODE_PRIVATE).edit();

            editor.putInt("ID", measurement.getUserID());
            if (measurement.getTimeStarted() > 0)   editor.putString("StartedAt", dateFormat.format(new Date(measurement.getTimeStarted())));
            if (measurement.getTimeFinished() > 0)  editor.putString("FinishedAt", dateFormat.format(new Date(measurement.getTimeFinished())));
            if (measurement.getTimeSent() > 0)      editor.putString("SentAt", dateFormat.format(new Date(measurement.getTimeSent())));
            editor.putString("Latitude", String.valueOf(measurement.getLastLatitude()));
            editor.putString("Longitude", String.valueOf(measurement.getLastLongitude()));
            editor.putString("Altitude", String.valueOf(measurement.getLastAltitude()));
            editor.putString("Temperature", String.valueOf(measurement.getAvgTemperature()));
            editor.putString("Humidity", String.valueOf(measurement.getAvgHumidity()));
            editor.putString("Pressure", String.valueOf(measurement.getAvgPressure()));
            editor.putString("Wind", String.valueOf(measurement.getAvgWind()));
            editor.putString("Orientation", String.valueOf(measurement.getOrientation()));

            editor.commit();
        }
        catch (Exception e) { bus.post(new WnService.ToastEvent("測量記錄儲存失敗\n" + e.getMessage() + " \n" + e.getStackTrace())); }
    }

    private static void readVersion1(String filename) {
        SharedPreferences sharedPref = WnService.context().getSharedPreferences(filename.replace(".xml", ""), Context.MODE_PRIVATE);

        WindooMeasurement measurement = new WindooMeasurement();
        measurement.setVersion(1);
        measurement.newMeasurementID();

        String  windooUserID = sharedPref.getString("ID", null),
                timeStarted = sharedPref.getString("StartedAt", null),
                timeFinished = sharedPref.getString("FinishedAt", null),
                timeSent = sharedPref.getString("SentAt", null),
                latitude = sharedPref.getString("Latitude", null),
                longitude = sharedPref.getString("Longitude", null),
                altitude = sharedPref.getString("Altitude", null),
                temperature = sharedPref.getString("Temperature", null),
                humidity = sharedPref.getString("Humidity", null),
                pressure = sharedPref.getString("Pressure", null),
                wind = sharedPref.getString("Wind", null),
                orientation = sharedPref.getString("Orientation", null);

        if(windooUserID != null) measurement.setWindooUserID(Integer.valueOf(windooUserID));
        measurement.setWindooID(WnSettings.getWindooID());

        try{ if(timeFinished != null)   measurement.setTimeFinished(dateFormat.parse(timeFinished).getTime()); }  catch(Exception e){}
        try{ if(timeStarted != null)    measurement.setTimeStarted(dateFormat.parse(timeStarted).getTime()); }    catch(Exception e){}
        try{ if(timeSent != null)       measurement.setTimeSent(dateFormat.parse(timeSent).getTime()); }          catch(Exception e){}

        Location location = new Location("");
        if(latitude != null) location.setLatitude(Double.valueOf(latitude));
        if(longitude != null) location.setLongitude(Double.valueOf(longitude));
        if(altitude != null) location.setAltitude(Double.valueOf(altitude));
        measurement.addLocation(location);

        if(wind != null)        measurement.addWind(measurement.getTimeFinished(), Double.valueOf(wind));
        if(temperature != null) measurement.addTemperature(measurement.getTimeFinished(), Double.valueOf(temperature));
        if(humidity != null)    measurement.addHumidity(measurement.getTimeFinished(), Double.valueOf(humidity));
        if(pressure != null)    measurement.addPressure(measurement.getTimeFinished(), Double.valueOf(pressure));
        if(orientation != null) measurement.setOrientation(Float.valueOf(orientation));

        history.put(measurement.getMeasurementID(), measurement);
    }

}