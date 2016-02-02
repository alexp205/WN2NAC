package org.cook_team.wn2nac;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import ch.skywatch.windoo.api.JDCWindooEvent;
import ch.skywatch.windoo.api.JDCWindooManager;
import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class WnWindoo implements Observer {

    private static final EventBus bus = EventBus.getDefault();

    private static WnWindoo observer;
    public static WnWindoo observer() { return observer; }
    private WnWindoo() {}

    /** WINDOO manager **/
    private static JDCWindooManager jdcWindooManager;
    public static boolean observing = false;

    /** WINDOO status **/
    public static boolean available = false;
    public static boolean calibrated = false;
    public static JDCWindooMeasurement liveMeasurement = new JDCWindooMeasurement();

    /** Init WINDOO **/
    public static void init() {
        observer = new WnWindoo();
        jdcWindooManager = JDCWindooManager.getInstance();
        jdcWindooManager.setToken(WnService.context().getResources().getString(R.string.token));
    }

    /** Start WINDOO **/
    public static void start() {
        jdcWindooManager.addObserver(observer);
        jdcWindooManager.enable(WnService.context());
        observing = true;
    }

    /** Stop WINDOO **/
    public static void stop () {
        jdcWindooManager.disable(WnService.context());
        jdcWindooManager.deleteObserver(observer);
        observing = false;
    }

    /** WINDOO event **/

    @Override
    public void update(Observable observable, final Object object) {
        WindooEvent windooEvent = new WindooEvent((JDCWindooEvent) object);
        switch(windooEvent.getType()) {
            case JDCWindooEvent.JDCWindooAvailable:
                available = true;
                break;
            case JDCWindooEvent.JDCWindooNotAvailable:
                available = false;
                calibrated = false;
                break;
            case JDCWindooEvent.JDCWindooCalibrated:
                available = true;
                calibrated = true;
                break;
            case JDCWindooEvent.JDCWindooNewWindValue :
                liveMeasurement.setWind((double) windooEvent.getData());
                if (WnMeasurement.measuring) WnMeasurement.wind.add((double) windooEvent.getData());
                break;
            case JDCWindooEvent.JDCWindooNewTemperatureValue :
                liveMeasurement.setTemperature((double) windooEvent.getData());
                if (WnMeasurement.measuring) WnMeasurement.temperature.add((double) windooEvent.getData());
                break;
            case JDCWindooEvent.JDCWindooNewHumidityValue :
                liveMeasurement.setHumidity((double) windooEvent.getData());
                if (WnMeasurement.measuring) WnMeasurement.humidity.add((double) windooEvent.getData());
                break;
            case JDCWindooEvent.JDCWindooNewPressureValue :
                liveMeasurement.setPressure((double) windooEvent.getData());
                if (WnMeasurement.measuring) WnMeasurement.pressure.add((double) windooEvent.getData());
                break;
            case JDCWindooEvent.JDCWindooPublishSuccess:
                break;
            case JDCWindooEvent.JDCWindooPublishException:
                break;
            case JDCWindooEvent.JDCWindooVolumeNotAtItsMaximum:
                break;
        }
        bus.post(windooEvent);
    }

}
