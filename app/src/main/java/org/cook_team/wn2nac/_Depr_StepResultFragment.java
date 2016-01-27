package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

public class _Depr_StepResultFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    private TextView avgWindTextView, avgTemperatureTextView, avgHumidityTextView, avgPressureTextView;

    private TextView networkTextView;
    private Button sendButton;

    public _Depr_StepResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout._depr_fragment_step_result, container, false);

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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.send:
                break;
        }
    }
}