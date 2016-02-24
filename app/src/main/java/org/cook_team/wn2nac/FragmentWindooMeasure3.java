package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

public class FragmentWindooMeasure3 extends android.support.v4.app.Fragment implements Switch.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    //private static EventBus bus = EventBus.getDefault();

    public static int min = 1, sec = 0;

    NumberPicker minPicker, secPicker;
    CheckBox checkBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_measure_3, container, false);

        minPicker = (NumberPicker) rootView.findViewById(R.id.minPicker);
        secPicker = (NumberPicker) rootView.findViewById(R.id.secPicker);
        minPicker.setMinValue(0); minPicker.setMaxValue(60);
        secPicker.setMinValue(0); secPicker.setMaxValue(59);
        minPicker.setOnValueChangedListener(this);
        secPicker.setOnValueChangedListener(this);

        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(this);

        updateDisplay();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        //bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        WnMeasure.vibrateOnFinish = isChecked;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        min = minPicker.getValue();
        sec = secPicker.getValue();
    }

    public void updateDisplay() {
        minPicker.setValue(min);
        secPicker.setValue(sec);
        checkBox.setChecked(WnMeasure.vibrateOnFinish);
    }

}