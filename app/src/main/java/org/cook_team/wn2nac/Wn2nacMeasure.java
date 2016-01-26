package org.cook_team.wn2nac;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacMeasure {

    /** EVENT BUS */
    private static EventBus bus = EventBus.getDefault();
    public Wn2nacMeasure() {
        if (!bus.isRegistered(this)) bus.register(this);
    }

    /** MEASUREMENT */
    public static boolean measuring = false;
    public static boolean measured = false;
    public static int min = 1, sec = 0;
    public static int duration = 60;
    private static CountDownTimer countDownTimer;
    public static long tick = 0;
    public static List<Double> wind, temperature, humidity, pressure;
    public static WindooMeasurement currentMeasurement = new WindooMeasurement();

    /** Measurement Start EVENT */
    public static class StartEvent {}
    public void onEventMainThread(StartEvent event) {
        if (!measuring) {
            currentMeasurement = new WindooMeasurement();
            currentMeasurement.setCreatedAt(new Date());
            wind = new ArrayList<>();
            temperature = new ArrayList<>();
            humidity = new ArrayList<>();
            pressure = new ArrayList<>();
            measuring = true;
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

    /** Measurement Finish EVENT */
    public static class FinishEvent {}
    public void onEventBackgroundThread(FinishEvent event) {
        currentMeasurement.setUpdatedAt(new Date());
        currentMeasurement.setNickname(Wn2nacPreferences.ID);
        currentMeasurement.setLatitude(Wn2nacLocation.lastLocation.getLatitude());
        currentMeasurement.setLongitude(Wn2nacLocation.lastLocation.getLongitude());
        currentMeasurement.setAltitude(Wn2nacLocation.lastLocation.getAltitude());

        measured = true; measuring = false;
        double avgWind = 0, avgTemperature = 0, avgHumidity = 0, avgPressure = 0;
        for (int i = 0; i < wind.size(); i++) avgWind += wind.get(i);
        avgWind /= wind.size();
        for (int i = 0; i < temperature.size(); i++) avgTemperature += temperature.get(i);
        avgTemperature /= temperature.size();
        for (int i = 0; i < humidity.size(); i++) avgHumidity += humidity.get(i);
        avgHumidity /= humidity.size();
        for (int i = 0; i < pressure.size(); i++) avgPressure += pressure.get(i);
        avgPressure /= pressure.size();

        currentMeasurement.setWind(avgWind);
        currentMeasurement.setTemperature(avgTemperature);
        currentMeasurement.setHumidity(avgHumidity);
        currentMeasurement.setPressure(avgPressure);
        bus.post(new UpdateDisplayEvent());
    }

    /** Measurement Abandon EVENT */
    public static class AbandonEvent {}
    public void onEventMainThread(AbandonEvent event) {
        measuring = false;
        countDownTimer.cancel();
        bus.post(new UpdateDisplayEvent());
    }

    /** Measurement UpdateDisplay EVENT */
    public static class UpdateDisplayEvent {}
}
