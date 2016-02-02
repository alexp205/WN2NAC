package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class FragmentWindooBar extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    private TextView status, wind, temperature, humidity, pressure;
    private Button button_start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_bar, container, false);

        button_start = (Button) rootView.findViewById(R.id.button_start);
        button_start.setOnClickListener(this);
        status = (TextView) rootView.findViewById(R.id.bar_status);
        wind = (TextView) rootView.findViewById(R.id.bar_wind);
        temperature = (TextView) rootView.findViewById(R.id.bar_temperature);
        humidity = (TextView) rootView.findViewById(R.id.bar_humidity);
        pressure = (TextView) rootView.findViewById(R.id.bar_pressure);

        updateDisplay();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new WnMeasurement.UpdateEvent());
        updateDisplay();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    public void updateDisplay() {
        if (WnWindoo.calibrated) status.setText("Windoo儀器已校正");
        else if (WnWindoo.available) status.setText("Windoo儀器已連接 (需校正)");
        else status.setText("Windoo儀器未連接");

        if (WnWindoo.liveMeasurement.hasWindSpeed())
            wind.setText(String.format("%.2f", (double) WnWindoo.liveMeasurement.getWind()));
        if (WnWindoo.liveMeasurement.hasTemperature())
            temperature.setText(String.format("%.2f", (double) WnWindoo.liveMeasurement.getTemperature()));
        if (WnWindoo.liveMeasurement.hasHumidity())
            humidity.setText(String.format("%.2f", (double) WnWindoo.liveMeasurement.getHumidity()));
        if (WnWindoo.liveMeasurement.hasPressure())
            pressure.setText(String.format("%.2f", (double) WnWindoo.liveMeasurement.getPressure()));
    }

    public void onEventMainThread(WindooEvent event) {
        if (event.getType() == JDCWindooEvent.JDCWindooAvailable)
            status.setText("已連接 (需校正)");
        else if (event.getType() == JDCWindooEvent.JDCWindooNotAvailable)
            status.setText("未連接");
        else if (event.getType() == JDCWindooEvent.JDCWindooCalibrated)
            status.setText("已校正");
        else if (event.getType() == JDCWindooEvent.JDCWindooVolumeNotAtItsMaximum)
            status.setText("請將音量調至最大");
        else if (event.getType() == JDCWindooEvent.JDCWindooPublishSuccess)
            status.setText("JDCWindooPublishSuccess : " + event.getData());
        else if (event.getType() == JDCWindooEvent.JDCWindooPublishException)
            status.setText("JDCWindooPublishException : " + event.getData());
        else if (event.getType() == JDCWindooEvent.JDCWindooNewWindValue)
            wind.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewTemperatureValue)
            temperature.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewHumidityValue)
            humidity.setText(String.format("%.2f", (double) event.getData()));
        else if (event.getType() == JDCWindooEvent.JDCWindooNewPressureValue)
            pressure.setText(String.format("%.2f", (double) event.getData()));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_start:
                if (!WnMap.measureFragmentVisible)
                    bus.post(new FragmentWindooMeasure.ShowEvent());
                else
                    bus.post(new FragmentWindooMeasure.HideEvent());
                break;
        }
    }

    public void onEventMainThread(FragmentWindooMeasure.ShowEvent event) {
        button_start.setText("回到地圖");
        WnMap.measureFragmentVisible = true;
    }

    public void onEventMainThread(FragmentWindooMeasure.HideEvent event) {
        button_start.setText("測量");
        WnMap.measureFragmentVisible = false;
    }

    public void onEventMainThread(WnMeasurement.UpdateEvent event) {
        if(WnMeasurement.measuring)
            button_start.setVisibility(View.INVISIBLE);
        else
            button_start.setVisibility(View.VISIBLE);
    }
}