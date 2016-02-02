package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class FragmentWindooMapMenu extends android.support.v4.app.Fragment implements View.OnClickListener {

    //private static EventBus bus = EventBus.getDefault();
    Button buttonCancel, buttonMenu, buttonRecord;
    LinearLayout menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_map_menu, container, false);

        menu = (LinearLayout) rootView.findViewById(R.id.menu);
        menu.setVisibility(View.INVISIBLE);
        buttonCancel = (Button) rootView.findViewById(R.id.buttonCancel);
        buttonMenu = (Button) rootView.findViewById(R.id.buttonMenu);
        buttonRecord = (Button) rootView.findViewById(R.id.buttonRecord);
        buttonCancel.setOnClickListener(this);
        buttonMenu.setOnClickListener(this);
        buttonRecord.setOnClickListener(this);

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
            case R.id.buttonCancel:
                menu.setVisibility(View.INVISIBLE);
                buttonMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonMenu:
                buttonMenu.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonRecord:
                break;
        }
    }

}