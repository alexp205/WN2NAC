package org.cook_team.wn2nac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.greenrobot.event.EventBus;

public class FragmentWindooMeasure extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    public static class ShowEvent {}
    public static class HideEvent {}

    public static int currentStep = 2;
    Button buttonLast, buttonNext, buttonClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_windoo_measure, container, false); // Inflate the layout for this fragment

        buttonLast  = (Button) rootView.findViewById(R.id.buttonLast);
        buttonNext  = (Button) rootView.findViewById(R.id.buttonNext);
        buttonClose = (Button) rootView.findViewById(R.id.buttonClose);
        buttonLast.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonClose.setOnClickListener(this);

        getChildFragmentManager().beginTransaction().replace(R.id.container, new FragmentWindooMeasure2()).commit();
        buttonLast.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateStep();
        if (WnMap.measureFragmentVisible)
            bus.post(new FragmentWindooMeasure.ShowEvent());
        else
            bus.post(new FragmentWindooMeasure.HideEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (!bus.isRegistered(this)) bus.register(this);
        updateDisplay();
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
                if (currentStep<4) currentStep++;
                else if (currentStep==4) currentStep = 2;
                //if (currentStep==4) bus.post(new WnMeasure.StartEvent());
                break;
            case R.id.buttonLast:
                if (currentStep==4) bus.post(new WnMeasure.AbandonEvent());
                if (currentStep>2) currentStep--;
                break;
            case R.id.buttonClose:
                bus.post(new HideEvent());
                break;
        }
        updateStep();
    }

    public void updateStep() {
        Fragment fragment = new FragmentWindooMeasure1();
        switch(currentStep) {
            case 1:
                buttonLast.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonNext.setText("下一步");
                fragment = new FragmentWindooMeasure1();
                break;
            case 2:
                WnMeasure.measuring = false;
                buttonLast.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonLast.setText("上一步");
                buttonNext.setText("下一步");
                fragment = new FragmentWindooMeasure2();
                break;
            case 3:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonLast.setText("上一步");
                buttonNext.setText("開始測量");
                fragment = new FragmentWindooMeasure3();
                break;
            case 4:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
                buttonLast.setText("取消測量");
                //bus.post(new WnMeasure.StartEvent());
                fragment = new FragmentWindooMeasuring();
                break;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void updateDisplay() {
        if(WnMeasure.measuring) {
            buttonLast.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
            buttonClose.setVisibility(View.INVISIBLE);
            buttonLast.setText("取消測量");
        }
        else {
            /*buttonLast.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonClose.setVisibility(View.VISIBLE);
            buttonNext.setText("重新測量");*/
            bus.post(new HideEvent());
            buttonLast.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonClose.setVisibility(View.VISIBLE);
            buttonNext.setText("下一步");
            getChildFragmentManager().beginTransaction().replace(R.id.container, new FragmentWindooMeasure2()).commit();
        }
    }

}