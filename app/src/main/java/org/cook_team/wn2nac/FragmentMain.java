package org.cook_team.wn2nac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.greenrobot.event.EventBus;

public class FragmentMain extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static EventBus bus = EventBus.getDefault();

    Button buttonLast, buttonNext;
    int currentPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        buttonLast = (Button) rootView.findViewById(R.id.buttonLast);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonLast.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        getChildFragmentManager().beginTransaction().replace(R.id.container, new FragmentWindooMeasure2()).commit();
        buttonLast.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            case R.id.buttonNext:
                if (currentPage<4) currentPage++;
                break;
            case R.id.buttonLast:
                if (currentPage>1) currentPage--;
                break;
        }
        updatePage();
    }

    public void updatePage() {
        Fragment fragment = new FragmentWindooMeasure1();
        switch(currentPage) {
            case 1:
                buttonLast.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                fragment = new FragmentWindooMeasure1();
                break;
            case 2:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                fragment = new FragmentWindooMeasure2();
                break;
            case 3:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                fragment = new FragmentWindooMeasure3();
                break;
            case 4:
                buttonLast.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);
                fragment = new FragmentWindooMeasuring();
                break;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}