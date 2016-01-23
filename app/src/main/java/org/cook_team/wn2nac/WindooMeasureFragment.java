package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class WindooMeasureFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    //private static EventBus bus = EventBus.getDefault();
    public static class ShowEvent {}
    public static class HideEvent {}

    Button buttonLast, buttonNext;
    WindooMeasureFragment1 windooMeasureFragment1;
    WindooMeasureFragment2 windooMeasureFragment2;
    WindooMeasureFragment3 windooMeasureFragment3;
    int currentStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_measure, container, false);

        buttonLast = (Button) rootView.findViewById(R.id.buttonLast);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonLast.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        windooMeasureFragment1 = (WindooMeasureFragment1) getChildFragmentManager().findFragmentById(R.id.windoo_measure_1);
        windooMeasureFragment2 = (WindooMeasureFragment2) getChildFragmentManager().findFragmentById(R.id.windoo_measure_2);
        windooMeasureFragment3 = (WindooMeasureFragment3) getChildFragmentManager().findFragmentById(R.id.windoo_measure_3);
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment2).commit();
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment3).commit();
        buttonLast.setVisibility(View.INVISIBLE);
        currentStep = 1;

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
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonNext:
                if (currentStep == 1) {
                    currentStep = 2;
                    buttonLast.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment1).commit();
                    getChildFragmentManager().beginTransaction().show(windooMeasureFragment2).commit();
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment3).commit();
                }
                else if (currentStep == 2) {
                    currentStep = 3;
                    buttonNext.setText("開始測量");
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment1).commit();
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment2).commit();
                    getChildFragmentManager().beginTransaction().show(windooMeasureFragment3).commit();
                }
                break;
            case R.id.buttonLast:
                if (currentStep == 3) {
                    currentStep = 2;
                    buttonNext.setText("下一步");
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment1).commit();
                    getChildFragmentManager().beginTransaction().show(windooMeasureFragment2).commit();
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment3).commit();

                }
                else if (currentStep == 2) {
                    currentStep = 1;
                    buttonLast.setVisibility(View.INVISIBLE);
                    getChildFragmentManager().beginTransaction().show(windooMeasureFragment1).commit();
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment2).commit();
                    getChildFragmentManager().beginTransaction().hide(windooMeasureFragment3).commit();
                }
                break;
        }
    }

}