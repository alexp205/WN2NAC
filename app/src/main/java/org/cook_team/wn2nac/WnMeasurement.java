package org.cook_team.wn2nac;

import android.app.Service;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class WnMeasurement {

    private static final EventBus bus = EventBus.getDefault();

    /** MEASUREMENT OPTIONS **/
    public static boolean vibrate = false;
    public static int min = 1, sec = 0;

    /** MEASUREMENT STATUS**/
    public static boolean hasHeading = false; public static float heading = -9999;
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
                    bus.post(new UpdateEvent());
                }
                public void onFinish() {
                    bus.post(new FinishEvent());
                }
            }.start();
        }
        bus.post(new UpdateEvent());
    }

    /** MEASUREMENT Finish**/
    public static class FinishEvent {}
    public static class FinishedEvent {}
    public static void finish() {
        measured = true; measuring = false;
        bus.post(new WnMeasurement.UpdateEvent());

        measurement.setUpdatedAt(new Date());
        measurement.setNickname(WnSettings.UserID);

        Location location = WnLocation.lastLocation;
        if (location != null) {
            measurement.setLatitude(location.getLatitude());
            measurement.setLongitude(location.getLongitude());
            measurement.setAltitude(location.getAltitude());
        }
        measurement.setOrientation(heading); heading = -9999; hasHeading = false;

        double sumWind = 0, sumTemperature = 0, sumHumidity = 0,  sumPressure = 0;
        for (Double val : wind) sumWind += val;
        for (Double val : temperature) sumTemperature += val;
        for (Double val : humidity) sumHumidity += val;
        for (Double val : pressure) sumPressure += val;
        measurement.setWind(sumWind / wind.size());
        measurement.setTemperature(sumTemperature / temperature.size());
        measurement.setHumidity(sumHumidity / humidity.size());
        measurement.setPressure(sumPressure / pressure.size());

        WnHistory.add(measurement);
        bus.post(new FinishedEvent()); // update UI
        if (vibrate) {
            Vibrator myVibrator = (Vibrator) WnService.context().getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            myVibrator.vibrate(300);
        }
        bus.post(new WnMeasurement.DoneEvent(measurement));
    }

    /** MEASUREMENT Abandon **/
    public static class AbandonEvent {}
    public static void abandon() {
        measuring = false;
        countDownTimer.cancel();
        bus.post(new UpdateEvent());
    }

    /** MEASUREMENT Update  **/
    public static class UpdateEvent {}
    public static class DoneEvent {
        public final WindooMeasurement measurement;
        public DoneEvent(WindooMeasurement measurement) {
            this.measurement = measurement;
        }
    }
}
