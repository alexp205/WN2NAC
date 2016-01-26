package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

public class StepResultFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    private TextView avgWindTextView, avgTemperatureTextView, avgHumidityTextView, avgPressureTextView;

    private TextView networkTextView;
    private Button sendButton;

    public StepResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_result, container, false);

        avgWindTextView = (TextView) rootView.findViewById(R.id.wind_result);
        avgTemperatureTextView = (TextView) rootView.findViewById(R.id.temperature_result);
        avgHumidityTextView = (TextView) rootView.findViewById(R.id.humidity_result);
        avgPressureTextView = (TextView) rootView.findViewById(R.id.pressure_result);

        networkTextView = (TextView) rootView.findViewById(R.id.network);
        sendButton = (Button) rootView.findViewById(R.id.send);
        sendButton.setOnClickListener(this);

        sendButton.setEnabled(Wn2nacMeasure.measured);

        //bus.post(new Wn2nacMeasure.MeasureDisplayEvent());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
       // if (!bus.isRegistered(this)) bus.register(this);

    }

    @Override
    public void onPause() {
        //bus.unregister(this);
        super.onPause();
    }

    /*public void onEventMainThread(Wn2nacMeasure.MeasureDisplayEvent event) {
        avgWindTextView.setText(String.format("%.2f", (double) Wn2nacMeasure.currentMeasurement.getWind()));
        avgTemperatureTextView.setText(String.format("%.2f", (double) Wn2nacMeasure.currentMeasurement.getTemperature()));
        avgHumidityTextView.setText(String.format("%.2f", (double) Wn2nacMeasure.currentMeasurement.getHumidity()));
        avgPressureTextView.setText(String.format("%.2f", (double) Wn2nacMeasure.currentMeasurement.getPressure()));
    }*/

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.send:
                bus.post(new Wn2nacNetwork.CheckNetworkEvent());
                bus.post(new Wn2nacNetwork.SendMeasurementEvent(Wn2nacHistory.measurement.size()-1));
                break;
        }
    }

    public void onEventMainThread(Wn2nacNetwork.NetworkEvent event) {
        networkTextView.setText(event.message);
    }

    public void onEventMainThread(Wn2nacNetwork.CheckNetworkEvent event) {
        networkTextView.setText("檢查網路中...");
    }

    public void onEventMainThread(Wn2nacNetwork.NetworkAvailableEvent event) {
        networkTextView.setText("網路可以使用");
    }

    public void onEventMainThread(Wn2nacNetwork.NetworkNotAvailableEvent event) {
        networkTextView.setText("網路無法使用");
    }

    public void onEventMainThread(Wn2nacNetwork.MeasurementSentEvent event) {
        sendButton.setEnabled(false);
    }
}