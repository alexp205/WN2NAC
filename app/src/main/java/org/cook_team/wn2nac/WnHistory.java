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
        save(measurement);
    }

    /** Read all measurement records **/
    public static void readAll() {
        try {
            history = new IndexedMap<>();
            File files[] = new File(WnService.context().getApplicationInfo().dataDir,"shared_prefs").listFiles();
            bus.post(new WnService.DebugEvent("[HISTORY] Reading..."));
            bus.post(new WnService.DebugEvent("\tFound " + String.valueOf(files.length) + " files:"));
            if(files != null) for (int i=files.length-1; i>=0; i--) {
                File file = files[i];
                bus.post(new WnService.DebugEvent("\t\tFile: " + file.getName()));
                if(file.getName().startsWith("org.cook_team.wn2nac.HISTORY.")) {
                    bus.post(new WnService.DebugEvent("\t\t\tReading " + file.getName()));
                    readVersion1(file.getName());
                }
            }
        } catch (Exception e) { bus.post(new WnService.DebugEvent("測量記錄讀取失敗\n" + e.getMessage()));  }
        bus.post(new DatasetChangedEvent());
    }

    public static void save(WindooMeasurement measurement) {
        saveVersion2(measurement);
        readAll();
        bus.post(new DatasetChangedEvent());
    }

    /** Save single measurement record **/
    public static void saveVersion2(WindooMeasurement measurement) {

            String filename = measurement.getFilename() != null ? measurement.getFilename() : "org.cook_team.wn2nac.HISTORY." + String.valueOf(measurement.getTimeFinished()) + ".xml";
        //bus.post(new WnService.ToastEvent(filename));
        SharedPreferences.Editor editor = WnService.context().getSharedPreferences(filename.replace(".xml", ""), Context.MODE_PRIVATE).edit();

            editor.putString("MeasurementID", measurement.getMeasurementID());
            editor.putString("Version", String.valueOf(2));
            editor.putString("ID", String.valueOf(measurement.getUserID()));
            editor.putString("StartedAt", String.valueOf(measurement.getTimeStarted()));
            editor.putString("FinishedAt", String.valueOf(measurement.getTimeFinished()));
            editor.putString("SentAt", String.valueOf(measurement.getTimeSent()));
            bus.post(new WnService.DebugEvent("[WRITE] Sent: " + String.valueOf(measurement.getTimeSent())));
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

    private static void readVersion1(String filename) {

        try {
            WindooMeasurement measurement = new WindooMeasurement();
            measurement.setFilename(filename);

            SharedPreferences sharedPref = WnService.context().getSharedPreferences(filename.replace(".xml", ""), Context.MODE_PRIVATE);

            measurement.setVersion(1);

            String measurementID = sharedPref.getString("MeasurementID", "");
            if (measurementID != "") measurement.setMeasurementID(measurementID);
            else
                measurement.setMeasurementID(filename.replace(".xml", "").replace("org.cook_team.wn2nac.HISTORY.", ""));

            String windooUserID;
            try {
                windooUserID = sharedPref.getString("ID", "0");
            } catch (Exception e) {
                windooUserID = String.valueOf(sharedPref.getInt("ID", 0));
            }

            String
                    timeStarted = sharedPref.getString("StartedAt", "0"),
                    timeFinished = sharedPref.getString("FinishedAt", "0"),
                    timeSent = sharedPref.getString("SentAt", "0"),
                    latitude = sharedPref.getString("Latitude", ""),
                    longitude = sharedPref.getString("Longitude", ""),
                    altitude = sharedPref.getString("Altitude", ""),
                    temperature = sharedPref.getString("Temperature", ""),
                    humidity = sharedPref.getString("Humidity", ""),
                    pressure = sharedPref.getString("Pressure", ""),
                    wind = sharedPref.getString("Wind", ""),
                    orientation = sharedPref.getString("Orientation", "");

            try {
                measurement.setWindooUserID(Integer.valueOf(windooUserID));
            } catch (Exception e) {
                measurement.setWindooUserID(0);
            }
            measurement.setWindooID(WnSettings.getWindooID());

            try {
                measurement.setTimeFinished(Long.valueOf(timeFinished));
                measurement.setTimeStarted(Long.valueOf(timeStarted));
                measurement.setTimeSent(Long.valueOf(timeSent));
                bus.post(new WnService.DebugEvent("\t\t\t[READ] Sent: " + String.valueOf(measurement.getTimeSent())));
            } catch (Exception e) { bus.post(new WnService.ToastEvent("TIME write error\n"+e.getMessage()+"\n"+e.getStackTrace()));}

            Location location = new Location("");
            if (!latitude.equals("")) location.setLatitude(Double.valueOf(latitude));
            if (!longitude.equals("")) location.setLongitude(Double.valueOf(longitude));
            if (!altitude.equals("")) location.setAltitude(Double.valueOf(altitude));
            measurement.addLocation(location);

            if (wind != "") {
                measurement.addWind(measurement.getTimeFinished(), Double.valueOf(wind));
                measurement.getWind().setAvg(Double.valueOf(wind));
            }
            if (temperature != "") {
                measurement.addTemperature(measurement.getTimeFinished(), Double.valueOf(temperature));
                measurement.getTemperature().setAvg(Double.valueOf(temperature));
            }
            if (humidity != "") {
                 measurement.addHumidity(measurement.getTimeFinished(), Double.valueOf(humidity));
                measurement.getHumidity().setAvg(Double.valueOf(humidity));
            }
            if (pressure != "") {
                measurement.addPressure(measurement.getTimeFinished(), Double.valueOf(pressure));
                measurement.getPressure().setAvg(Double.valueOf(pressure));
            }
            if (orientation != "") measurement.setOrientation(Float.valueOf(orientation));

            history.put(measurement.getMeasurementID(), measurement);
            //bus.post(new WnService.DebugEvent(String.valueOf(history.keys.size())));
        } catch (Exception e) { bus.post(new WnService.ToastEvent("測量記錄儲存失敗\n" + e.getMessage() + " \n" + e.getStackTrace())); }
    }

}