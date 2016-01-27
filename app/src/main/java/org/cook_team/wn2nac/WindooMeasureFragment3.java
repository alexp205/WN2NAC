package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public class WindooMeasureFragment3 extends android.support.v4.app.Fragment implements Switch.OnCheckedChangeListener,NumberPicker.OnValueChangeListener {

    //private static EventBus bus = EventBus.getDefault();

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
        minPicker.setValue(Wn2nacMeasure.min);
        secPicker.setValue(Wn2nacMeasure.sec);
        minPicker.setOnValueChangedListener(this);
        secPicker.setOnValueChangedListener(this);

        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setChecked(Wn2nacMeasure.vibrate);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        //bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Wn2nacMeasure.vibrate = isChecked;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Wn2nacMeasure.min = minPicker.getValue();
        Wn2nacMeasure.sec = secPicker.getValue();
    }

}