package org.cook_team.wn2nac;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.UUID;

public class WindooMeasurement implements Comparable<WindooMeasurement> {

    /** DATE FORMAT **/
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    /** INTERNAL: Measurement METADATA**/
    private int version = 2; // Measurement data structure version
    private String measurementID;
    private WindooUser windooUser = WnSettings.getWindooUser();
    private int windooID = WnSettings.getWindooID();

    /** INTERNAL: Measurement DATA**/
    private long timeStarted = 0, timeFirstWindooData = 0, timeFinished = 0, timeSent = 0;
    private ArrayList<Location> location = new ArrayList<>();
    private float orientation = -9999.0f;
    private IndexedMap<Long, Double> temperature = new IndexedMap<>(),
                                humidity = new IndexedMap<>(),
                                pressure = new IndexedMap<>(),
                                wind = new IndexedMap<>();
    private double avgTemperature, avgHumidity, avgPressure, avgWind;
    //picture, pictureGUID

    /** COMPARATOR **/
    @Override
    public int compareTo(@NonNull WindooMeasurement measurement) {
        return (int) (this.timeStarted - measurement.getTimeStarted());
    }

    /** GETTERs **/
    public int getVersion() { return version; }
    public String getMeasurementID() { return measurementID; }
    public int getUserID() { return windooUser.getUserID(); }
    public int getWindooID() { return windooID; }
    public long  getTimeStarted() { return timeStarted; }
    public long  getTimeFirstWindooData() { return timeFirstWindooData; }
    public long getTimeFinished() { return timeFinished; }
    public long getTimeSent() { return timeSent; }
    public ArrayList<Location> getLocation() { return location; }
    public Float getOrientation()     { return orientation; }
    public Double getAvgTemperature()   { return avgTemperature; }
    public Double getAvgHumidity()      { return avgHumidity; }
    public Double getAvgPressure()      { return avgPressure; }
    public Double getAvgWind()          { return avgWind; }
    public IndexedMap<Long, Double> getTemperature()   { return temperature; }
    public IndexedMap<Long, Double> getHumidity()      { return humidity; }
    public IndexedMap<Long, Double> getPressure()      { return pressure; }
    public IndexedMap<Long, Double> getWind()          { return wind; }

    /** additional GETTERs **/
    public Location getLastLocation()   { return location.get(location.size() - 1); }
    public Double getLastLatitude()     { return getLastLocation().getLatitude(); }
    public Double getLastLongitude()    { return getLastLocation().getLongitude(); }
    public Double getLastAltitude()     { return getLastLocation().getAltitude(); }
    public Double getLastTemperature()  { return temperature.getLast(); }
    public Double getLastHumidity()     { return humidity.getLast(); }
    public Double getLastPressure()     { return pressure.getLast(); }
    public Double getLastWind()         { return wind.getLast(); }

    /** SETTERs **/
    public void setVersion(int version)     { this.version = version; }
    public void newMeasurementID()          { measurementID = UUID.randomUUID().toString(); }
    public void setWindooUserID(int userID) { windooUser.setUserID(userID);}
    public void setWindooID(int windooID)   { this.windooID = windooID;}
    public void setTimeStarted(long timeStarted)    { this.timeStarted = timeStarted; }
    public void setTimeFinished(long timeFinished)  { this.timeFinished = timeFinished; }
    public void setTimeSent(long timeSent)          { this.timeSent = timeSent; }
    public void setOrientation(float orientation) { this.orientation = orientation; }

    /** ADD DATA **/
    public void addTemperature(long time, Double val)   { temperature.put(time, val); }
    public void addHumidity(long time, Double val)      { humidity.put(time, val); }
    public void addPressure(long time, Double val)      { pressure.put(time, val); }
    public void addWind(long time, Double val)          { wind.put(time, val); }
    public void addTemperature(Double val)   { long now = System.currentTimeMillis(); addTemperature(now, val); if(timeFirstWindooData == 0) timeFirstWindooData = now; }
    public void addHumidity(Double val)      { long now = System.currentTimeMillis(); addHumidity(now, val); if(timeFirstWindooData == 0) timeFirstWindooData = now; }
    public void addPressure(Double val)      { long now = System.currentTimeMillis(); addPressure(now, val); if(timeFirstWindooData == 0) timeFirstWindooData = now; }
    public void addWind(Double val)          { long now = System.currentTimeMillis(); addWind(now, val); if(timeFirstWindooData == 0) timeFirstWindooData = now;; }
    public void addLocation(Location newLocation) { location.add(newLocation); }
    public void addLatLng(double latitude, double longitude) {
        Location newLocation = new Location("Manual");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setTime(System.currentTimeMillis());
        location.add(newLocation);
    }

    /** START measurement **/
    public void start() {
        location.add(WnLocation.lastLocation);
        timeStarted = System.currentTimeMillis();
    }

    public void calcAverages() {
        // Calculate averages
        avgTemperature = avgHumidity = avgPressure = avgWind = 0;
        for (double val: temperature.values())    avgTemperature += val;
        for (double val: humidity.values())       avgHumidity += val;
        for (double val: pressure.values())       avgPressure += val;
        for (double val: wind.values())           avgWind += val;
        avgTemperature /= temperature.size();
        avgHumidity /= humidity.size();
        avgPressure /= pressure.size();
        avgWind /= wind.size();
    }

    /** FINISH measurement **/
    public boolean finish() {
        if (temperature.size()>0 && humidity.size()>0 && pressure.size()>0 && wind.size()>0 ) {
            newMeasurementID();
            timeFinished = System.currentTimeMillis();
            calcAverages();
            return true;
        }
        else return false;
    }

    /** DISPLAY IN MAP MARKER **/
    public MarkerOptions toMarkerOptions() {
        return new MarkerOptions()
                .position(new LatLng(getLastLatitude(), getLastLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(measurementID)
                .snippet(dateFormat.format(new Date(timeStarted)) + "," +
                        timeFormat.format(new Date(timeStarted)) + " ~ " + timeFormat.format(new Date(timeFinished)) + "," +
                        String.format("%.2f", avgTemperature) + "," +
                        String.format("%.2f", avgHumidity) + "," +
                        String.format("%.2f", avgPressure) + "," +
                        String.format("%.2f", avgWind));
    }
}
