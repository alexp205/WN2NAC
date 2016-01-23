package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.greenrobot.event.EventBus;

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private static EventBus bus = EventBus.getDefault();
    WindooMeasureFragment windooMeasureFragment;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        windooMeasureFragment = (WindooMeasureFragment) getChildFragmentManager().findFragmentById(R.id.windoo_measure);
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment).commit();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the My Location layer
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng NTUAS = new LatLng(25.014852, 121.538715);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(NTUAS));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    public void onEventMainThread(WindooMeasureFragment.ShowEvent event) {
        getChildFragmentManager().beginTransaction().show(windooMeasureFragment).commit();
    }

    public void onEventMainThread(WindooMeasureFragment.HideEvent event) {
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment).commit();
    }
}
