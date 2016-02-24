package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;
import me.grantland.widget.AutofitTextView;

public class FragmentWindooBar extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();

    private AutofitTextView status, wind, temperature, humidity, pressure;
    private Button button_start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_windoo_bar, container, false); // Inflate the layout for this fragment

        status      = (AutofitTextView) rootView.findViewById(R.id.bar_status);
        wind        = (AutofitTextView) rootView.findViewById(R.id.bar_wind);
        temperature = (AutofitTextView) rootView.findViewById(R.id.bar_temperature);
        humidity    = (AutofitTextView) rootView.findViewById(R.id.bar_humidity);
        pressure    = (AutofitTextView) rootView.findViewById(R.id.bar_pressure);

        button_start = (Button) rootView.findViewById(R.id.button_start);
        button_start.setOnClickListener(this);

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
        status.setText(getResources().getStringArray(R.array.windooStatus)[WnObserver.getWindooStatus()]);
        temperature.setText(WnObserver.getLastTemperatureString());
        humidity.setText(WnObserver.getLastHumidityString());
        pressure.setText(WnObserver.getLastPressureString());
        wind.setText(WnObserver.getLastWindString());

        if(WnMeasure.measuring) button_start.setVisibility(View.INVISIBLE);
        else button_start.setVisibility(View.VISIBLE);
    }
    public void onEventMainThread(WnObserver.WindooEvent event) { updateDisplay(); }

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
}