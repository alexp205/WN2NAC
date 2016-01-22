package org.cook_team.wn2nac;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.skywatch.windoo.api.JDCWindooEvent;
import de.greenrobot.event.EventBus;

public class StepMeasureFragment extends android.support.v4.app.Fragment implements View.OnClickListener, TextWatcher {

    private static EventBus bus = EventBus.getDefault();

    private Button minPlusButton, minMinusButton, secPlusButton, secMinusButton, measureButton;
    private EditText minEditText, secEditText;
    private TextView countdownTextView, hintTextView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_measure, container, false);

        minEditText = (EditText) rootView.findViewById(R.id.minEditText);
        secEditText = (EditText) rootView.findViewById(R.id.secEditText);
        minPlusButton = (Button) rootView.findViewById(R.id.minPlusButton);
        minMinusButton = (Button) rootView.findViewById(R.id.minMinusButton);
        secPlusButton = (Button) rootView.findViewById(R.id.secPlusButton);
        secMinusButton = (Button) rootView.findViewById(R.id.secMinusButton);
        measureButton = (Button) rootView.findViewById(R.id.buttonMeasure);
        countdownTextView = (TextView) rootView.findViewById(R.id.countdownTextView);
        hintTextView = (TextView) rootView.findViewById(R.id.hintTextView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        minPlusButton.setOnClickListener(this);
        minMinusButton.setOnClickListener(this);
        secPlusButton.setOnClickListener(this);
        secMinusButton.setOnClickListener(this);
        measureButton.setOnClickListener(this);

        minEditText.addTextChangedListener(this);
        secEditText.addTextChangedListener(this);

        updateDisplay();

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
            case R.id.secMinusButton:
                if(Wn2nacMeasure.duration > 0) Wn2nacMeasure.duration --;
                break;
            case R.id.secPlusButton:
                if(Wn2nacMeasure.duration < Wn2nacMeasure.maxDuration) Wn2nacMeasure.duration ++;
                break;
            case R.id.minMinusButton:
                if(Wn2nacMeasure.duration - 60 > 0) Wn2nacMeasure.duration -= 60;
                break;
            case R.id.minPlusButton:
                if(Wn2nacMeasure.duration + 60 < Wn2nacMeasure.maxDuration) Wn2nacMeasure.duration += 60;
                break;
            case R.id.buttonMeasure:
                if (!Wn2nacMeasure.measuring)
                    bus.post(new Wn2nacMeasure.MeasureEvent());
                else
                    bus.post(new Wn2nacMeasure.MeasureAbandonEvent());
                break;
        }
        updateDisplay();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}
    @Override
    public void afterTextChanged(Editable s) {
        try{
            int min = Integer.parseInt(String.valueOf(minEditText.getText()));
            int sec = Integer.parseInt(String.valueOf(secEditText.getText()));
            Wn2nacMeasure.duration = min * 60 + sec;
        }
        catch (Exception ex) {}
    }

    private void updateDisplay() {
        minEditText.removeTextChangedListener(this);
        secEditText.removeTextChangedListener(this);
        minEditText.setText(String.valueOf(Wn2nacMeasure.duration / 60));
        secEditText.setText(String.valueOf(Wn2nacMeasure.duration % 60));
        minEditText.addTextChangedListener(this);
        secEditText.addTextChangedListener(this);

        if (!Wn2nacPreferences.IDset) {
            hintTextView.setText("ID 尚未設定\n請先設定ID");
            measureButton.setEnabled(false);
        } else {
            hintTextView.setText("請保持Windoo儀器直立");
            measureButton.setEnabled(true);
            //measureButton.setEnabled(Wn2nacService.calibrated);
        }

        if (Wn2nacMeasure.measuring) measureButton.setText("放棄量測");
        else measureButton.setText("開始量測");

        if (Wn2nacMeasure.measured) {
            progressBar.setProgress(100);
            countdownTextView.setText("量測完成，請傳送量測結果");
        }
        else if (!Wn2nacMeasure.measuring) {
            if (Wn2nacService.calibrated) countdownTextView.setText("Windoo 已校正，請開始量測");
            else if (Wn2nacService.available) countdownTextView.setText("Windoo 尚未校正");
            else countdownTextView.setText("Windoo 未連接");
        }
    }

    public void onEventMainThread(Wn2nacMeasure.MeasureTickEvent event) {
        countdownTextView.setText("量測中... 請勿移動\n" + String.valueOf(event.tick / 1000 / 60) + ":" + String.valueOf(event.tick / 1000 % 60));
        progressBar.setProgress(100 - 100 * (int) event.tick / 1000 / Wn2nacMeasure.currentDuration);
    }

    public void onEventMainThread(Wn2nacMeasure.MeasureDisplayEvent event) {
        updateDisplay();
    }
}