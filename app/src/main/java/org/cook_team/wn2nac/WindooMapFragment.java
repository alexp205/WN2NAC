package org.cook_team.wn2nac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;

import de.greenrobot.event.EventBus;

public class WindooMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private static EventBus bus = EventBus.getDefault();
    WindooMeasureFragment windooMeasureFragment;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new Wn2nacLocation.LocationEnableEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new Wn2nacLocation.LocationEnableEvent());
    }

    @Override
    public void onPause() {
        bus.post(new Wn2nacLocation.LocationDisableEvent());
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_map, container, false);

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

    LatLng NTUAS = new LatLng(25.014852, 121.538715);
    LatLng userPos = null;
    Marker userMarker;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the My Location layer
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(NTUAS));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        userPos = new LatLng(Wn2nacLocation.lastLocation.getLatitude(),Wn2nacLocation.lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        /*userMarker = mMap.addMarker(new MarkerOptions()
                .position(userPos)
                .title("使用者")
                .snippet("ID: 0"));*/

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents (Marker marker) {
                return null;
            }
            @Override
            public View getInfoWindow (Marker marker) {
                String[] data = marker.getSnippet().split(",");
                if(data.length == 5) {
                    View window = getActivity().getLayoutInflater().inflate(R.layout.fragment_windoo_measured, null);
                    TextView time = (TextView) window.findViewById(R.id.time);
                    TextView temperature = (TextView) window.findViewById(R.id.temperature);
                    TextView pressure = (TextView) window.findViewById(R.id.pressure);
                    TextView humidity = (TextView) window.findViewById(R.id.humidity);
                    TextView wind = (TextView) window.findViewById(R.id.wind);
                    time.setText(data[0]);
                    temperature.setText(data[1]);
                    pressure.setText(data[2]);
                    humidity.setText(data[3]);
                    wind.setText(data[4]);
                    return window;
                }
                else return null;
            }
        });
    }

    public void onEventMainThread(WindooMeasureFragment.ShowEvent event) {
        getChildFragmentManager().beginTransaction().show(windooMeasureFragment).commit();
    }

    public void onEventMainThread(WindooMeasureFragment.HideEvent event) {
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment).commit();
    }

    public void onEventMainThread(Wn2nacLocation.LocationUpdateEvent event) {
        /*bus.post(new Wn2nacService.ToastEvent(
                String.valueOf(Wn2nacLocation.lastLocation.getLatitude()) + "\n" +
                        String.valueOf(Wn2nacLocation.lastLocation.getLongitude()) + "\n" +
                        String.valueOf(Wn2nacLocation.lastLocation.getAltitude()) + "\n" +
                        String.valueOf(Wn2nacLocation.lastLocation.getAccuracy()) + "\n" +
                        String.valueOf(Wn2nacLocation.lastLocation.getProvider())
        ));*/
        userPos = new LatLng(Wn2nacLocation.lastLocation.getLatitude(),Wn2nacLocation.lastLocation.getLongitude());
        userMarker.setPosition(userPos);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public void onEventMainThread(Wn2nacMeasure.UpdateDisplayEvent event) {
        LatLng pos = new LatLng(Wn2nacMeasure.currentMeasurement.getLatitude(), Wn2nacMeasure.currentMeasurement.getLongitude());
        Marker measurement = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(String.valueOf(Wn2nacMeasure.currentMeasurement.getSeq()))
                .snippet(dateFormat.format(Wn2nacMeasure.currentMeasurement.getUpdatedAt()) + "\n" +
                                timeFormat.format(Wn2nacMeasure.currentMeasurement.getUpdatedAt()) + "," +
                                String.valueOf(Wn2nacMeasure.currentMeasurement.getTemperature()) + "," +
                                String.valueOf(Wn2nacMeasure.currentMeasurement.getPressure()) + "," +
                                String.valueOf(Wn2nacMeasure.currentMeasurement.getHumidity()) + "," +
                                String.valueOf(Wn2nacMeasure.currentMeasurement.getWind())
                ));
        measurement.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }
}
