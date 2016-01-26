package org.cook_team.wn2nac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class WindooMeasureFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    public static class ShowEvent {}
    public static class HideEvent {}

    Button buttonLast, buttonNext, buttonClose;
    int currentStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_measure, container, false);

        buttonLast = (Button) rootView.findViewById(R.id.buttonLast);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonClose = (Button) rootView.findViewById(R.id.buttonClose);
        buttonLast.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonClose.setOnClickListener(this);
        getChildFragmentManager().beginTransaction().replace(R.id.container, new WindooMeasureFragment1()).commit();
        buttonLast.setVisibility(View.INVISIBLE);
        currentStep = 1;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonNext:
                if (currentStep<4) currentStep++;
                else if (currentStep==4) currentStep = 1;
                break;
            case R.id.buttonLast:
                if (currentStep==4) bus.post(new Wn2nacMeasure.AbandonEvent());
                if (currentStep>1) currentStep--;
                break;
            case R.id.buttonClose:
                bus.post(new HideEvent());
                break;
        }
        Fragment fragment = new WindooMeasureFragment1();
        switch(currentStep) {
            case 1:
                buttonLast.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonNext.setText("下一步");
                fragment = new WindooMeasureFragment1();
                break;
            case 2:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonLast.setText("上一步");
                buttonNext.setText("下一步");
                fragment = new WindooMeasureFragment2();
                break;
            case 3:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonLast.setText("上一步");
                buttonNext.setText("開始測量");
                fragment = new WindooMeasureFragment3();
                break;
            case 4:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
                buttonLast.setText("取消測量");
                fragment = new WindooMeasuringFragment();
                break;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void onEventMainThread(Wn2nacMeasure.UpdateDisplayEvent event) {
        if(Wn2nacMeasure.measuring) {
            buttonLast.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
            buttonClose.setVisibility(View.INVISIBLE);
            buttonLast.setText("取消測量");
        }
        else if(Wn2nacMeasure.measured) {
            /*buttonLast.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonClose.setVisibility(View.VISIBLE);
            buttonNext.setText("重新測量");*/
            bus.post(new HideEvent());
            buttonLast.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonClose.setVisibility(View.VISIBLE);
            buttonNext.setText("下一步");
            getChildFragmentManager().beginTransaction().replace(R.id.container, new WindooMeasureFragment1()).commit();
        }
    }

}