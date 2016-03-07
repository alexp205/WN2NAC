package org.cook_team.wn2nac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class FragmentWindooMeasure extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();
    public static class ShowEvent {}
    public static class HideEvent {}
    public static class ToggleEvent {}

    private static int minStep = 1, maxStep = 4;
    private static int currentStep = minStep;
    private static boolean visible = false;

    private Button buttonLast, buttonNext, buttonClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateVisibility();
        switchStep(currentStep);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        updateVisibility();
        switchStep(currentStep);
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
                if (currentStep < maxStep) currentStep++;
                if (currentStep == 4) bus.post(new WnMeasure.StartEvent(FragmentWindooMeasure3.min*60 + FragmentWindooMeasure3.sec));
                break;
            case R.id.buttonLast:
                if (currentStep > minStep) currentStep--;
                if (currentStep == 3) bus.post(new WnMeasure.AbandonEvent());
                break;
            case R.id.buttonClose:
                bus.post(new HideEvent());
                break;
        }
        switchStep(currentStep);
    }

    private void switchStep(int step) {
        Fragment fragment;
        switch(step) {
            default:
            case 1:
                buttonNext.setText("下一步");
                fragment = new FragmentWindooMeasure1();
                break;
            case 2:
                WnMeasure.measuring = false;
                buttonLast.setText("上一步");
                buttonNext.setText("下一步");
                fragment = new FragmentWindooMeasure2();
                break;
            case 3:
                buttonLast.setText("上一步");
                buttonNext.setText("開始測量");
                fragment = new FragmentWindooMeasure3();
                break;
            case 4:
                buttonLast.setText("取消測量");
                fragment = new FragmentWindooMeasuring();
                break;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        buttonLast.setVisibility(step > minStep ? View.VISIBLE : View.INVISIBLE);
        buttonNext.setVisibility(step < maxStep ? View.VISIBLE : View.INVISIBLE);
        buttonClose.setVisibility(step < maxStep ? View.VISIBLE : View.INVISIBLE);
        buttonNext.setEnabled(currentStep != 3 || WnObserver.getWindooStatus() == WnObserver.WINDOO_CALIBRATED);
    }

    public void onEventMainThread(ShowEvent event) {
        visible = true;
    }
    public void onEventMainThread(HideEvent event) {
        visible = false;
    }
    public void onEventMainThread(ToggleEvent event) {
        visible = !visible;
        updateVisibility();
    }
    private void updateVisibility() {
        bus.post(visible ? new FragmentWindooMeasure.ShowEvent() : new FragmentWindooMeasure.HideEvent());
    }

    public void onEventMainThread(WnObserver.WindooEvent event) {
        switch(event.getType()) {
            case JDCWindooEvent.JDCWindooAvailable:
                break;
            case JDCWindooEvent.JDCWindooNotAvailable:
                if (currentStep == 3) buttonNext.setEnabled(false);
                    break;
            case JDCWindooEvent.JDCWindooCalibrated:
                if (currentStep == 3) buttonNext.setEnabled(true);
        }
    }

    public void onEventMainThread(WnMeasure.AbandonEvent event) {
        switchStep(3);
    }
}