package org.cook_team.wn2nac;

import android.app.Service;
import android.os.CountDownTimer;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Wn2nacMeasure {

    private static EventBus bus = EventBus.getDefault();

    /** MEASUREMENT OPTIONS **/
    public static boolean hasHeading = false;
    public static float heading = -9999;
    public static boolean vibrate = false;
    public static int min = 1, sec = 0;

    /** MEASUREMENT STATUS**/
    public static boolean measuring = false;
    public static boolean measured = false;
    public static CountDownTimer countDownTimer;
    public static int duration = 60;
    public static long tick = 0;
    public static List<Double> wind, temperature, humidity, pressure;
    public static WindooMeasurement measurement = new WindooMeasurement();

    /** MEASUREMENT Start**/
    public static class StartEvent {}
    public static void start() {
        if (!measuring) {
            measuring = true;
            measurement = new WindooMeasurement();
            measurement.setCreatedAt(new Date());
            wind = new ArrayList<>(); temperature = new ArrayList<>(); humidity = new ArrayList<>(); pressure = new ArrayList<>();

            duration = min*60 + sec;
            countDownTimer = new CountDownTimer(duration*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tick = millisUntilFinished;
                    bus.post(new UpdateDisplayEvent());
                }
                public void onFinish() {
                    bus.post(new FinishEvent());
                }
            }.start();
        }
        bus.post(new UpdateDisplayEvent());
    }

    /** MEASUREMENT Finish**/
    public static class FinishEvent {}
    public static void finish() {

        measured = true; measuring = false;

        measurement.setUpdatedAt(new Date());
        measurement.setNickname(Wn2nacPreferences.ID);
        measurement.setLatitude(Wn2nacMap.lastLocation.getLatitude());
        measurement.setLongitude(Wn2nacMap.lastLocation.getLongitude());
        measurement.setAltitude(Wn2nacMap.lastLocation.getAltitude());
        measurement.setOrientation(heading); heading = -9999; hasHeading = false;

        double avgWind = 0, avgTemperature = 0, avgHumidity = 0, avgPressure = 0;
        for (int i = 0; i < wind.size(); i++) avgWind += wind.get(i);
        avgWind /= wind.size();
        for (int i = 0; i < temperature.size(); i++) avgTemperature += temperature.get(i);
        avgTemperature /= temperature.size();
        for (int i = 0; i < humidity.size(); i++) avgHumidity += humidity.get(i);
        avgHumidity /= humidity.size();
        for (int i = 0; i < pressure.size(); i++) avgPressure += pressure.get(i);
        avgPressure /= pressure.size();

        measurement.setWind(avgWind);
        measurement.setTemperature(avgTemperature);
        measurement.setHumidity(avgHumidity);
        measurement.setPressure(avgPressure);

        measurement.setSeq(Wn2nacHistory.nextSeq++);
        Wn2nacPreferences.write();
        Wn2nacHistory.history.add(0, measurement);
        bus.post(new Wn2nacHistory.RefreshEvent());

        bus.post(new Wn2nacHistory.SaveEvent(measurement));
        bus.post(new Wn2nacNetwork.SendMeasurementEvent(measurement));
        bus.post(new UpdateDisplayEvent());
        bus.post(new DoneEvent());
    }

    /** MEASUREMENT Abandon **/
    public static class AbandonEvent {}
    public static void abandon() {
        measuring = false;
        countDownTimer.cancel();
        bus.post(new UpdateDisplayEvent());
    }

    /** MEASUREMENT Update display **/
    public static class UpdateDisplayEvent {}
    public static class DoneEvent {}
}
