package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

public class FragmentWindooMeasure1 extends android.support.v4.app.Fragment {

    //private static EventBus bus = EventBus.getDefault();

    private FragmentWindooGraph fragmentWindooChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_measure_1, container, false);

        fragmentWindooChart = (FragmentWindooGraph) getChildFragmentManager().findFragmentById(R.id.windooChart);
        fragmentWindooChart.controlsBarVisible = false;
        fragmentWindooChart.chartBarVisible = false;
        fragmentWindooChart.pressureToggleButton.setChecked(false);
        fragmentWindooChart.windToggleButton.setChecked(false);
        fragmentWindooChart.onClick(fragmentWindooChart.pressureToggleButton);
        fragmentWindooChart.initView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
}