package org.cook_team.wn2nac;

import android.content.SyncRequest;
import android.util.SparseArray;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import ch.skywatch.windoo.api.JDCWindooEvent;
import ch.skywatch.windoo.api.JDCWindooManager;
import de.greenrobot.event.EventBus;

public class WnObserver extends WindooMeasurement implements Observer {

    private static final EventBus bus = EventBus.getDefault();
    /** WnObserver **/
    private static WnObserver instance = new WnObserver(); // Singleton instance
    private WnObserver(){
        if (!bus.isRegistered(this)) bus.register(this);
        jdcWindooManager = JDCWindooManager.getInstance();
        jdcWindooManager.setToken(WnApp.getContext().getString(R.string.token));
    }
    public static WnObserver getInstance() { return instance; }

    /** Windoo MANAGER **/
    private static JDCWindooManager jdcWindooManager;
    private static boolean observing = false;

    /** Windoo STATUS **/
    public static final int    WINDOO_NOT_AVAILABLE = 0,
                                WINDOO_AVAILABLE = 1,
                                WINDOO_CALIBRATED = 2;
    private static int windooStatus = WINDOO_NOT_AVAILABLE;
    public static int getWindooStatus() { return windooStatus; }
    private static String reading(double val) { return String.format("%.2f", val) ; }
    public static String getLastTemperatureString() { return instance.getTemperature().size()>0 ? reading(instance.getLastTemperature()) : WnApp.getContext().getString(R.string.val_null); }
    public static String getLastHumidityString()    { return instance.getHumidity().size()>0 ? reading(instance.getLastHumidity()) : WnApp.getContext().getString(R.string.val_null); }
    public static String getLastPressureString()    { return instance.getPressure().size()>0 ? reading(instance.getLastPressure()) : WnApp.getContext().getString(R.string.val_null); }
    public static String getLastWindString()        { return instance.getWind().size()>0 ? reading(instance.getLastWind()) : WnApp.getContext().getString(R.string.val_null); }

    /** START Windoo **/
    public static class StartEvent {}
    public void onEventBackgroundThread(StartEvent event) {
        jdcWindooManager.addObserver(instance);
        jdcWindooManager.enable(WnService.context());
        observing = true;
        start();
    }

    /** STOP Windoo **/
    public static class StopEvent {}
    public void onEventBackgroundThread(StopEvent event) {
        jdcWindooManager.disable(WnService.context());
        jdcWindooManager.deleteObserver(instance);
        observing = false;
    }

    /** WINDOO EVENT **/
    public class WindooEvent extends JDCWindooEvent {
        final Date time;
        public WindooEvent(JDCWindooEvent event) {
            this.type = event.getType();
            this.data = event.getData();
            this.time = new Date();
        }
    }
    @Override
    public void update(Observable observable, final Object object) {
        WindooEvent windooEvent = new WindooEvent((JDCWindooEvent) object);
        switch(windooEvent.getType()) {
            case JDCWindooEvent.JDCWindooAvailable:
                windooStatus = WINDOO_AVAILABLE;
                break;
            case JDCWindooEvent.JDCWindooNotAvailable:
                windooStatus = WINDOO_NOT_AVAILABLE;
                break;
            case JDCWindooEvent.JDCWindooCalibrated:
                windooStatus = WINDOO_CALIBRATED;
                break;
            case JDCWindooEvent.JDCWindooPublishSuccess:
                // TODO
                break;
            case JDCWindooEvent.JDCWindooPublishException:
                // TODO
                break;
            case JDCWindooEvent.JDCWindooVolumeNotAtItsMaximum:
                // TODO
                break;
        }

        if(windooStatus == WINDOO_CALIBRATED)
            switch(windooEvent.getType()) {
            case JDCWindooEvent.JDCWindooNewWindValue :
                addWind((Double) windooEvent.getData());
                //bus.post(new WnService.DebugEvent("Wind: "+ String.valueOf(windooEvent.getData())));
                break;
            case JDCWindooEvent.JDCWindooNewTemperatureValue :
                addTemperature((Double) windooEvent.getData());
                //bus.post(new WnService.DebugEvent("Temperature: " + String.valueOf(windooEvent.getData())));
                break;
            case JDCWindooEvent.JDCWindooNewHumidityValue :
                addHumidity((Double) windooEvent.getData());
                //bus.post(new WnService.DebugEvent("Humidity: " + String.valueOf(windooEvent.getData())));
                break;
            case JDCWindooEvent.JDCWindooNewPressureValue :
                addPressure((Double) windooEvent.getData());
                //bus.post(new WnService.DebugEvent("Pressure: " + String.valueOf(windooEvent.getData())));
                break;
        }
        bus.post(windooEvent);
    }
}
