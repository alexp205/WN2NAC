package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import de.greenrobot.event.EventBus;

public class FragmentWindooMeasuring extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();

    DonutProgress donut_progress;
    TextView countdown;

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

        bus.post(new WnMeasurement.UpdateEvent());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new WnMeasurement.UpdateEvent());
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
    }

    public void onEventMainThread(WnMeasurement.UpdateEvent event) {
        refreshView();
    }

    private void refreshView() {
        if(WnMeasurement.measuring) {
            countdown.setText("測量中... \n請勿移動\n" + String.format("%02d", WnMeasurement.tick / 1000 / 60) + ":" + String.format("%02d", WnMeasurement.tick / 1000 % 60));
            donut_progress.setProgress(100 - 100 * (int) WnMeasurement.tick / 1000 / WnMeasurement.duration);
        }
        else if (WnMeasurement.measured) {
            countdown.setText("測量完畢\n\n");
            donut_progress.setProgress(100);
        }
    }
}