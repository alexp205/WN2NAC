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
    public static int duration = 60;
    public static int currentDuration = 0;
    public static int maxDuration = 3600;
    public static int progress = 0;
    public static List<Double> wind, temperature, humidity, pressure;
    public static WindooMeasurement currentMeasurement = new WindooMeasurement();
    private static CountDownTimer countDownTimer;

    /** Measurement Start EVENT */
    public static class MeasureEvent {}

    public void onEventMainThread(MeasureEvent event) {
        currentMeasurement = new WindooMeasurement();
        currentMeasurement.setCreatedAt(new Date());
        wind = new ArrayList<>();
        temperature = new ArrayList<>();
        humidity = new ArrayList<>();
        pressure = new ArrayList<>();
        measuring = true;
        currentDuration = duration;
        countDownTimer = new CountDownTimer(currentDuration*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                bus.post(new MeasureTickEvent(millisUntilFinished));
            }
            public void onFinish() {
                bus.post(new MeasureFinishEvent());
            }
        }.start();
        bus.post(new MeasureDisplayEvent());
    }

    /** Measurement Tick EVENT */
    public static class MeasureTickEvent {
        public final long tick;
        public MeasureTickEvent(long tick) {
            this.tick = tick;
        }
    }

    /** Measurement Finish EVENT */
    public static class MeasureFinishEvent {}
    public void onEventBackgroundThread(MeasureFinishEvent event) {
        currentMeasurement.setUpdatedAt(new Date());
        currentMeasurement.setNickname(Wn2nacPreferences.ID);
        currentMeasurement.setLatitude(Wn2nacLocation.lastLocation.getLatitude());
        currentMeasurement.setLongitude(Wn2nacLocation.lastLocation.getLongitude());
        currentMeasurement.setAltitude(Wn2nacLocation.lastLocation.getAltitude());

        measured = true; measuring = false; progress = 100;
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

        bus.post(new MeasureSaveEvent());
        bus.post(new MeasureDisplayEvent());
    }

    /** Measurement Abandon EVENT */
    public static class MeasureAbandonEvent {}
    public void onEventMainThread(MeasureAbandonEvent event) {
        measuring = false;
        countDownTimer.cancel();
        bus.post(new MeasureDisplayEvent());
    }

    /** Measurement Display EVENT */
    public static class MeasureDisplayEvent {}
    /** Measurement Save EVENT */
    public static class MeasureSaveEvent {}

    public void onEventMainThread(Wn2nacNetwork.MeasurementSentEvent event) {
        measured = false;
        bus.post(new MeasureDisplayEvent());
    }
}
