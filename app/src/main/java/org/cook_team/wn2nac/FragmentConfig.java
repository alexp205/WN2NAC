package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import de.greenrobot.event.EventBus;

public class FragmentConfig extends android.support.v4.app.Fragment implements View.OnClickListener, Switch.OnCheckedChangeListener {

    private static EventBus bus = EventBus.getDefault();

    private EditText idEditText, windooEditText;
    private Button applyButton;
    private CheckBox debugOn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_config, container, false);

        idEditText      = (EditText) rootView.findViewById(R.id.idEditText);
        windooEditText  = (EditText) rootView.findViewById(R.id.windooEditText);
        applyButton = (Button) rootView.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(this);
        debugOn = (CheckBox) rootView.findViewById(R.id.checkBoxDebug);
        debugOn.setOnCheckedChangeListener(this);
        debugOn.setChecked(WnSettings.debugOn);

        idEditText.setText(String.valueOf(WnSettings.getWindooUserID()));
        windooEditText.setText(String.valueOf(WnSettings.getWindooID()));

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
            case R.id.applyButton:
                WnSettings.setWindooUserID(Integer.valueOf(String.valueOf(idEditText.getText())));
                WnSettings.setWindooID(Integer.valueOf(String.valueOf(windooEditText.getText())));
                WnSettings.save();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.checkBoxDebug:
                //if (isChecked) getChildFragmentManager().beginTransaction().show(fragmentWindooMeasure).commit();
                //else getChildFragmentManager().beginTransaction().hide(fragmentWindooMeasure).commit();
                WnSettings.debugOn = isChecked;
                if (WnSettings.debugOn) bus.post(new ActivityMain.DebugOnEvent());
                else bus.post(new ActivityMain.DebugOffEvent());
        }
    }
}
