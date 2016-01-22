package org.cook_team.wn2nac;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Observable;
import java.util.Observer;

import ch.skywatch.windoo.api.JDCWindooEvent;
import ch.skywatch.windoo.api.JDCWindooManager;
import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacService extends Service implements Observer {

    public static boolean available = false;
    public static boolean calibrated = false;

    public static Context context;
    public static Wn2nacMeasure wn2NacMeasure = new Wn2nacMeasure();
    public static Wn2nacHistory wn2NacHistory = new Wn2nacHistory();
    public static Wn2nacNetwork wn2NacNetwork = new Wn2nacNetwork();
    public static Wn2nacLocation wn2NacLocation = new Wn2nacLocation();
    public static Wn2nacException wn2NacException = new Wn2nacException();

    @Override
    public void onCreate() {
        context = this;
        if (!bus.isRegistered(this)) bus.register(this);
        init();
        Wn2nacPreferences.read();
        Wn2nacHistory.read();
        Wn2nacNetwork.init();
        Wn2nacLocation.init();
        bus.post(new StartObservationEvent());
        bus.post(new Wn2nacLocation.LocationFetchEvent());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bus.post(new StartObservationEvent());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        bus.post(new StopObservationEvent());
        //bus.post(new ToastEvent("Windoo service ended"));
        bus.unregister(this);
    }

    private static EventBus bus = EventBus.getDefault();

    public static int currentStep = 0;

    /**  OBSERVATION **/

    boolean observing = false;

    private static JDCWindooManager jdcWindooManager;
    private static WindooEvent currentEvent;
    public static JDCWindooMeasurement liveMeasurement = new JDCWindooMeasurement();

    public static class StartObservationEvent {}
    public static class StopObservationEvent {}
    public static class ToggleObservationEvent {}

    public void init() {
        jdcWindooManager = JDCWindooManager.getInstance();
        jdcWindooManager.setToken(getResources().getString(R.string.token));
    }

    public void onEventBackgroundThread(StartObservationEvent event) {
        jdcWindooManager.addObserver(this);
        jdcWindooManager.enable(Wn2nacService.this);
        observing = true;
    }

    public void onEventBackgroundThread(StopObservationEvent event) {
        jdcWindooManager.disable(Wn2nacService.this);
        jdcWindooManager.deleteObserver(this);
        observing = false;
    }

    public void onEventBackgroundThread(ToggleObservationEvent event) {
        if (observing) bus.post(new StopObservationEvent());
        else bus.post(new StartObservationEvent());
    }

    @Override
    public void update(Observable observable, final Object object) {
        currentEvent = new WindooEvent((JDCWindooEvent) object);
        if (currentEvent.getType() == JDCWindooEvent.JDCWindooNotAvailable) {
            calibrated = false;
        }
        else if (currentEvent.getType() == JDCWindooEvent.JDCWindooCalibrated) {
            bus.post(new WindooCalibratedEvent());
            calibrated = true;
        }
        else if (currentEvent.getType() == JDCWindooEvent.JDCWindooNewWindValue) {
            liveMeasurement.setWind((double) currentEvent.getData());
            if (Wn2nacMeasure.measuring) Wn2nacMeasure.wind.add(liveMeasurement.getWind());
        }
        else if (currentEvent.getType() == JDCWindooEvent.JDCWindooNewTemperatureValue) {
            liveMeasurement.setTemperature((double) currentEvent.getData());
            if (Wn2nacMeasure.measuring) Wn2nacMeasure.temperature.add(liveMeasurement.getTemperature());
        }
        else if (currentEvent.getType() == JDCWindooEvent.JDCWindooNewHumidityValue) {
            liveMeasurement.setHumidity((double) currentEvent.getData());
            if (Wn2nacMeasure.measuring) Wn2nacMeasure.humidity.add(liveMeasurement.getHumidity());
        }
        else if (currentEvent.getType() == JDCWindooEvent.JDCWindooNewPressureValue) {
            liveMeasurement.setPressure((double) currentEvent.getData());
            if (Wn2nacMeasure.measuring) Wn2nacMeasure.pressure.add(liveMeasurement.getPressure());
        }
        bus.post(currentEvent);
    }

    public static class WindooCalibratedEvent {}

    /** MESSAGING **/

    public static class ToastEvent {
        public final String message;
        public ToastEvent(String message) {
            this.message = message;
        }
    }
    public static class DebugEvent {
        public final String message;
        public DebugEvent(String message) {
            this.message = message;
        }
    }
    public static class MessageEvent {
        public final String message;
        public MessageEvent(String message) {
            this.message = message;
        }
    }
}


