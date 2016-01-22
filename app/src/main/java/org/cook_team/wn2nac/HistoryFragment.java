package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import de.greenrobot.event.EventBus;

public class HistoryFragment extends android.support.v4.app.Fragment implements ExpandableListView.OnChildClickListener {

    private static EventBus bus = EventBus.getDefault();

    ExpandableListView expListView;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        expListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        expListView.setAdapter(Wn2nacService.wn2NacHistory);
        expListView.setOnChildClickListener(this);

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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (childPosition == 9) {
            bus.post(new Wn2nacNetwork.SendMeasurementEvent(groupPosition));
        }
        return true;
    }

    public void onEventMainThread(Wn2nacNetwork.NetworkEvent event) {
        bus.post(new Wn2nacService.ToastEvent(event.message));
    }
}

