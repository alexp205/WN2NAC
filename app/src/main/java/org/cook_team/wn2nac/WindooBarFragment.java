package org.cook_team.wn2nac;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class WindooBarFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

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
        updateDisplay();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    public void updateDisplay() {
        if (Wn2nacService.calibrated) status.setText("Windoo儀器已校正");
        else if (Wn2nacService.available) status.setText("Windoo儀器已連接 (需校正)");
        else status.setText("Windoo儀器未連接");

        if (Wn2nacService.liveMeasurement.hasWindSpeed())
            wind.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getWind()));
        if (Wn2nacService.liveMeasurement.hasTemperature())
            temperature.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getTemperature()));
        if (Wn2nacService.liveMeasurement.hasHumidity())
            humidity.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getHumidity()));
        if (Wn2nacService.liveMeasurement.hasPressure())
            pressure.setText(String.format("%.2f", (double) Wn2nacService.liveMeasurement.getPressure()));
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

    boolean measureFragmentVisible = false;
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_start:
                if (!measureFragmentVisible)
                    bus.post(new WindooMeasureFragment.ShowEvent());
                else
                    bus.post(new WindooMeasureFragment.HideEvent());
                break;
        }
    }

    public void onEventMainThread(WindooMeasureFragment.ShowEvent event) {
        button_start.setText("回到地圖");
        measureFragmentVisible = true;
    }

    public void onEventMainThread(WindooMeasureFragment.HideEvent event) {
        button_start.setText("測量");
        measureFragmentVisible = false;
    }

    public void onEventMainThread(Wn2nacMeasure.UpdateDisplayEvent event) {
        if(Wn2nacMeasure.measuring)
            button_start.setVisibility(View.INVISIBLE);
        else
            button_start.setVisibility(View.VISIBLE);
    }
}