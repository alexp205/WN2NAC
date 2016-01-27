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

import de.greenrobot.event.EventBus;

public class _Depr_StepMeasureFragment extends android.support.v4.app.Fragment implements TextWatcher {

    private static EventBus bus = EventBus.getDefault();

    private Button minPlusButton, minMinusButton, secPlusButton, secMinusButton, measureButton;
    private EditText minEditText, secEditText;
    private TextView countdownTextView, hintTextView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout._depr_fragment_step_measure, container, false);

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

        minEditText.addTextChangedListener(this);
        secEditText.addTextChangedListener(this);

        updateDisplay();

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
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}
    @Override
    public void afterTextChanged(Editable s) {
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
}