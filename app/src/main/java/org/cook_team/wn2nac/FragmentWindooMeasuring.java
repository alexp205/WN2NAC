package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class FragmentWindooMeasuring extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();

    private DonutProgress donut_progress;
    private TextView countdown;

    private FragmentWindooGraph fragmentWindooChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_measuring, container, false);

        donut_progress = (DonutProgress) rootView.findViewById(R.id.donut_progress);
        countdown = (TextView) rootView.findViewById(R.id.countdown);

        fragmentWindooChart = (FragmentWindooGraph) getChildFragmentManager().findFragmentById(R.id.windooChart);
        fragmentWindooChart.controlsBarVisible = false;
        fragmentWindooChart.chartBarVisible = false;
        fragmentWindooChart.initView();

        updateView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        updateView();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
    }

    public void onEventMainThread(WnMeasure.StartedEvent event) {
        updateView();
    }
    public void onEventMainThread(WnMeasure.TickEvent event) {
        updateView();
    }
    public void onEventMainThread(WnMeasure.FinishedEvent event) {
        updateView();
    }
    public void onEventMainThread(WnMeasure.AbandonedEvent event) {
        updateView();
    }

    private void updateView() {
        if(WnMeasure.measuring) {
            countdown.setText("測量中... \n請勿移動\n" + String.format("%02d", WnMeasure.getLastTick() / 1000 / 60) + ":" + String.format("%02d", WnMeasure.getLastTick() / 1000 % 60));
            donut_progress.setProgress((int) (100 - 100 *  WnMeasure.getLastTick() / 1000 / WnMeasure.getDuration()));
        }
        else {
            countdown.setText("測量完畢\n\n");
            donut_progress.setProgress(100);
        }
    }

    public void onEventMainThread(WnObserver.WindooEvent event) {
        switch(event.getType()) {
            case JDCWindooEvent.JDCWindooAvailable:
                break;
            case JDCWindooEvent.JDCWindooNotAvailable:
                bus.post(new WnMeasure.AbandonEvent());
                bus.post(new WnService.ToastEvent("Windoo儀器未連接，測量失敗"));
                break;
        }
    }
}