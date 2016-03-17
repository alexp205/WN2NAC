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

import de.greenrobot.event.EventBus;

public class FragmentWindooMap extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private static EventBus bus = EventBus.getDefault();
    private FragmentWindooMeasure fragmentWindooMeasure;
    private FragmentWindooMapMenu fragmentWindooMapMenu;
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
        bus.post(new WnLocation.EnableEvent());
    }

    @Override
    public void onPause() {
        if(mMap != null) WnMap.mCam = mMap.getCameraPosition();
        bus.post(new WnLocation.DisableEvent());
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_map, container, false);

        fragmentWindooMeasure = (FragmentWindooMeasure) getChildFragmentManager().findFragmentById(R.id.windoo_measure);
        fragmentWindooMapMenu = (FragmentWindooMapMenu) getChildFragmentManager().findFragmentById(R.id.windoo_map_menu);
        getChildFragmentManager().beginTransaction().hide(fragmentWindooMeasure).commit();
        getChildFragmentManager().beginTransaction().hide(fragmentWindooMapMenu).commit();

        WnLocation.enable();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bus.post(new WnLocation.EnableEvent());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public class WindooInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (marker.getSnippet() != null) {
                String[] data = marker.getSnippet().split(",");
                if (data.length == 6) {
                    View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.fragment_windoo_measured, null);
                    TextView    time = (TextView) infoWindow.findViewById(R.id.time),
                                time2 = (TextView) infoWindow.findViewById(R.id.time2),
                                temperature = (TextView) infoWindow.findViewById(R.id.temperature),
                                pressure = (TextView) infoWindow.findViewById(R.id.pressure),
                                humidity = (TextView) infoWindow.findViewById(R.id.humidity),
                                wind = (TextView) infoWindow.findViewById(R.id.wind);
                    time.setText(data[0]);
                    time2.setText(data[1]);
                    temperature.setText(data[2]);
                    pressure.setText(data[3]);
                    humidity.setText(data[4]);
                    wind.setText(data[5]);
                    return infoWindow;
                }
            }
            return null;
        }
    }

    /** Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app. */
    
    Marker userMarker;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true); // Enable "My Location" layer.
        mMap.setInfoWindowAdapter(new WindooInfoWindowAdapter()); // WindooInfoWindowAdapter defined above.

        if (!WnMap.init) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(WnMap.NTUAS));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

            if (WnLocation.lastLocation != null) {
                WnMap.userPos = new LatLng(WnLocation.lastLocation.getLatitude(), WnLocation.lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(WnMap.userPos));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }

            WnMap.mCam = mMap.getCameraPosition();
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(WnMap.mCam));
        }

        /** MARKERS **/

        /** TODO: Use "Map" for history and makers */

        /*userMarker = mMap.addMarker(new MarkerOptions()
                .position(userPos)
                .title("使用者")
                .snippet("ID: 0"));*/

        // User measurement history markers
        for (int i=0; i<WnHistory.size(); i++)
            mMap.addMarker(WnHistory.get(i).toMarkerOptions());

        if (WnMap.goTo != null) {
            WindooMeasurement measurement = WnMap.goTo;
            Marker marker = mMap.addMarker(measurement.toMarkerOptions());
            LatLng pos = new LatLng(measurement.getLastLatitude(), measurement.getLastLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            marker.showInfoWindow();
            WnMap.goTo = null;
        }
    }

    public void onEventMainThread(FragmentWindooMeasure.ShowEvent event) {
        getChildFragmentManager().beginTransaction().show(fragmentWindooMeasure).commit();
    }

    public void onEventMainThread(FragmentWindooMeasure.HideEvent event) {
        getChildFragmentManager().beginTransaction().hide(fragmentWindooMeasure).commit();
    }

    public void onEventMainThread(WnLocation.NewLocationEvent event) {
        WnMap.userPos = new LatLng(WnLocation.lastLocation.getLatitude(), WnLocation.lastLocation.getLongitude());
    }

    public void onEventMainThread(WnMeasure.FinishedEvent event) {
        WindooMeasurement measurement = WnMeasure.measurement;
        //bus.post(new WnService.ToastEvent(String.valueOf(measurement.getLastLocation())));
        //bus.post(new WnService.ToastEvent(String.valueOf(measurement.getLastLatitude()) + "\n" + String.valueOf(measurement.getLastLongitude())));
        LatLng pos = new LatLng(measurement.getLastLatitude(), measurement.getLastLongitude());
        Marker marker = mMap.addMarker(WnMeasure.measurement.toMarkerOptions());
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        bus.post(new WnLocation.EnableEvent());
    }
}
