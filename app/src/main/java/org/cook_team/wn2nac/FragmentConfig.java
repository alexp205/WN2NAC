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

    public FragmentConfig() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_config, container, false);

        idEditText = (EditText) rootView.findViewById(R.id.idEditText);
        windooEditText = (EditText) rootView.findViewById(R.id.windooEditText);
        applyButton = (Button) rootView.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(this);
        debugOn = (CheckBox) rootView.findViewById(R.id.checkBoxDebug);
        debugOn.setOnCheckedChangeListener(this);
        debugOn.setChecked(WnSettings.debugOn);

        idEditText.setText(WnSettings.UserID);
        windooEditText.setText(WnSettings.WindooID);

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
                WnSettings.UserID = String.valueOf(idEditText.getText());
                WnSettings.WindooID = String.valueOf(windooEditText.getText());
                WnSettings.save();
                WnService.toast("設定已儲存");
                //bus.post(new WnMap.OpenEvent());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.checkBoxDebug:
                WnSettings.debugOn = isChecked;
        }
    }
}
