package org.cook_team.wn2nac;

import android.os.CountDownTimer;

import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class WnMeasure {

    private static final EventBus bus = EventBus.getDefault();

    /** WnMeasure **/
    private static WnMeasure instance = new WnMeasure(); // Singleton instance
    private WnMeasure(){
        if (!bus.isRegistered(this)) bus.register(this);
    }

    /** Measurement OPTIONS **/
    public static boolean vibrateOnFinish = false;
    public static boolean sendOnFinish = true;

    /** Measurement COUNTDOWN **/
    private static CountDownTimer countDownTimer;
    private static int duration = 60;
    private static long lastTick;
    public static long getLastTick() { return lastTick; }
    public static long getDuration() { return duration; }

    /** Measurement STATUS **/
    public static boolean measuring = false;
    public static WindooMeasurement measurement = new WindooMeasurement();

    /** Measurement START**/
    public static class StartEvent {
        final int duration;
        public StartEvent(int duration) { this.duration = duration; }
    }
    public static class StartedEvent {}
    public void onEventMainThread(StartEvent event) {
    if (!measuring) {
            measuring = true;
            measurement.start();
            duration = event.duration;
            countDownTimer = new CountDownTimer(duration*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    lastTick = millisUntilFinished;
                    bus.post(new TickEvent());
                }
                public void onFinish() { bus.post(new FinishEvent()); }
            }.start();
        }
        bus.post(new StartedEvent());
    }
    /** Measurement TICK  **/
    public static class TickEvent {}

    /** Measurement WINDOO EVENT **/
    public void onEventMainThread(WnObserver.WindooEvent event) {
        if (measuring && WnObserver.getWindooStatus() == WnObserver.WINDOO_CALIBRATED) switch (event.getType()) {
            case JDCWindooEvent.JDCWindooNewWindValue:
                measurement.addWind((Double) event.getData());
                break;
            case JDCWindooEvent.JDCWindooNewTemperatureValue:
                measurement.addTemperature((Double) event.getData());
                break;
            case JDCWindooEvent.JDCWindooNewHumidityValue:
                measurement.addHumidity((Double) event.getData());
                break;
            case JDCWindooEvent.JDCWindooNewPressureValue:
                measurement.addPressure((Double) event.getData());
                break;
        }
    }

    /** Measurement FINISH**/
    public static class FinishEvent {}
    public static class FinishedEvent {}
    public void onEventMainThread(FinishEvent event) { finish(); }
    public void finish() {
        if(measurement.finish()) {
            measuring = false;
            WnHistory.add(measurement);
            if (sendOnFinish)   { bus.post(new WnNetwork.SendEvent(measurement)); }
            if (vibrateOnFinish) { bus.post(new WnService.VibrateEvent(300)); }
            bus.post(new FinishedEvent());
        }
        else abandon();
    }

    /** MEASUREMENT Abandon **/
    public static class AbandonEvent {}
    public static class AbandonedEvent {}
    public void onEventMainThread(AbandonEvent event) { abandon();}
    public void abandon() {
        measuring = false;
        countDownTimer.cancel();
        bus.post(new AbandonedEvent());
    }

}
