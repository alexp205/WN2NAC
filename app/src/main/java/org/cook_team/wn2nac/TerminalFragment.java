package org.cook_team.wn2nac;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class TerminalFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private EventBus bus = EventBus.getDefault();

    private ScrollView scrollView; private boolean autoScroll = true;
    private TextView textView;
    private Button buttonToggleAutoScroll, buttonPause, buttonClear, buttonCheckNetwork, buttonSyncTime;

    public TerminalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_terminal, container, false);

        textView = (TextView) rootView.findViewById(R.id.terminalTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        scrollView = (ScrollView) rootView.findViewById(R.id.terminalScrollView);
        buttonToggleAutoScroll = (Button) rootView.findViewById(R.id.buttonToggleAutoScroll);
        buttonToggleAutoScroll.setOnClickListener(this);
        buttonPause = (Button) rootView.findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(this);
        buttonClear = (Button) rootView.findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(this);
        buttonCheckNetwork = (Button) rootView.findViewById(R.id.buttonCheckNetwork);
        buttonCheckNetwork.setOnClickListener(this);
        buttonSyncTime = (Button) rootView.findViewById(R.id.buttonSyncTime);
        buttonSyncTime.setOnClickListener(this);

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
            case R.id.buttonToggleAutoScroll:
                autoScroll = !autoScroll;
                break;
            case R.id.buttonPause:
                bus.post(new Wn2nacService.ToggleObservationEvent());
                break;
            case R.id.buttonClear:
                textView.setText("");
                scrollView.fullScroll(View.FOCUS_DOWN);
                break;
            case R.id.buttonCheckNetwork:
                break;
            case R.id.buttonSyncTime:
                break;
        }
    }

    public void onEventMainThread(Wn2nacService.DebugEvent event) {
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(Wn2nacService.MessageEvent event) {
        textView.append(event.message + "\n");
        if(autoScroll) scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void onEventMainThread(WindooEvent event) {
        textView.append(event.message() + "\n");
        if(autoScroll) scrollView.fullScroll(View.FOCUS_DOWN);
    }
}