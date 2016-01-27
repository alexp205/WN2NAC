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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WindooMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private static EventBus bus = EventBus.getDefault();
    WindooMeasureFragment windooMeasureFragment;
    WindooMapMenuFragment windooMapMenuFragment;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new Wn2nacMap.LocationEnableEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
        bus.post(new Wn2nacMap.LocationEnableEvent());
    }

    @Override
    public void onPause() {
        Wn2nacMap.mCam = mMap.getCameraPosition();
        bus.post(new Wn2nacMap.LocationDisableEvent());
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_windoo_map, container, false);

        windooMeasureFragment = (WindooMeasureFragment) getChildFragmentManager().findFragmentById(R.id.windoo_measure);
        windooMapMenuFragment = (WindooMapMenuFragment) getChildFragmentManager().findFragmentById(R.id.windoo_map_menu);
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment).commit();
        getChildFragmentManager().beginTransaction().hide(windooMapMenuFragment).commit();

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

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the My Location layer
        mMap.setMyLocationEnabled(true);

        if (!Wn2nacMap.init) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(NTUAS));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

            if (Wn2nacMap.lastLocation != null) {
                userPos = new LatLng(Wn2nacMap.lastLocation.getLatitude(), Wn2nacMap.lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }

            Wn2nacMap.mCam = mMap.getCameraPosition();
            Wn2nacMap.init = true;
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Wn2nacMap.mCam));
        }

        mMap.addMarker(new MarkerOptions().position(new LatLng(25.03299235490906, 121.5340119318952)).title("D1"));

        /*userMarker = mMap.addMarker(new MarkerOptions()
                .position(userPos)
                .title("使用者")
                .snippet("ID: 0"));*/

        /** MARKERS **/
        List<Marker> Dx = new ArrayList<>();
        List<Marker> Nx = new ArrayList<>();
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0329923549091, 121.534011931895)).title("D1")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0301541723664, 121.538244311073)).title("D2")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0326129071451, 121.537152594938)).title("D3")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0301541723664, 121.538244311073)).title("D4")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0310516072806, 121.536308195846)).title("D5")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0302520911265, 121.536969256222)).title("D6")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0299530205242, 121.535593079416)).title("D7")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0316468259215, 121.536486126853)).title("D8")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0316468259215, 121.536486126853)).title("D9")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0287684456665, 121.537073559872)).title("D10")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0282973208622, 121.537169062929)).title("D11")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0282973208622, 121.537169062929)).title("D12")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0267884716168, 121.536843629944)).title("D13")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0329677866779, 121.532062578798)).title("D14")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0315337399284, 121.53195206768)).title("D15")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0302648844192, 121.532842278641)).title("D16")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0343456756733, 121.532541175299)).title("D17")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0274300959169, 121.533108487747)).title("D18")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0293311588571, 121.532104913024)).title("D19")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0262582207957, 121.533803707043)).title("D20")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0244378720724, 121.533435183477)).title("D21")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0243553352097, 121.535986775423)).title("D22")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0254181141568, 121.53742619731)).title("D23")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0239558549795, 121.537358318392)).title("D24")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0243545244781, 121.539436012594)).title("D25")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0251691629367, 121.539149552329)).title("D26")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0265264770582, 121.539385679243)).title("D27")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0277076571599, 121.539609422281)).title("D28")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.029055297413, 121.539344932882)).title("D29")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0305758354509, 121.539544242665)).title("D30")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0315660906828, 121.539486296339)).title("D31")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0333701688627, 121.539382864724)).title("D32")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0341698096025, 121.539474245483)).title("D33")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0342214314331, 121.537996939419)).title("D34")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0343773886961, 121.535798430818)).title("D35")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0342760274366, 121.533699068039)).title("D36")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0126665403801, 121.537799540228)).title("N1")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0134870380797, 121.539182838279)).title("N2")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0150256018249, 121.541363657152)).title("N3")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0163687185327, 121.542785668261)).title("N4")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.017501087193, 121.544398232349)).title("N5")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0140980618084, 121.53808034225)).title("N6")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0144355202896, 121.538666422563)).title("N7")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0156175877718, 121.540458014295)).title("N8")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0173273032241, 121.542104738363)).title("N9")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0182922982113, 121.543876905076)).title("N10")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0142071550959, 121.536121356415)).title("N11")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.015685053652, 121.537692065089)).title("N12")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0167935793632, 121.539328254493)).title("N13")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0179705962129, 121.541658153329)).title("N14")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0198591820844, 121.543555727735)).title("N15")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0154101022513, 121.534606341804)).title("N16")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0166581445601, 121.536472679091)).title("N17")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0176311893729, 121.538228025764)).title("N18")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0194071220161, 121.539905519485)).title("N19")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0207274284725, 121.541654256864)).title("N20")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0167137963224, 121.533737951626)).title("N21")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.017502936446, 121.535812474167)).title("N22")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0184848066864, 121.537553086858)).title("N23")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.020177524727, 121.538751806022)).title("N24")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.021258769162, 121.540080783764)).title("N25")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0180560951687, 121.534278530774)).title("N26")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0189494758811, 121.535135134841)).title("N27")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0197733880344, 121.536935399161)).title("N28")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0212940775801, 121.537305722503)).title("N29")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.019533412195, 121.534802217277)).title("N30")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0208024211434, 121.535676573663)).title("N31")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0218815631409, 121.536929917941)).title("N32")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0210093891945, 121.53489709382)).title("N33")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0213281640737, 121.533689102585)).title("N34")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0180523725877, 121.53956170494)).title("N35")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0164571245005, 121.533822584269)).title("N36")));
        for (Marker d : Dx) {
            d.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            d.showInfoWindow();
        }
        for (Marker n : Nx) {
            n.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            n.showInfoWindow();
        }
        for (WindooMeasurement measurement : Wn2nacHistory.history) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(measurement.getLatitude(), measurement.getLongitude()))
                    .title(String.valueOf(measurement.getSeq()))
                    .snippet(dateFormat.format(measurement.getCreatedAt()) + "," +
                            timeFormat.format(measurement.getCreatedAt()) + " ~ " + timeFormat.format(measurement.getUpdatedAt()) + "," +
                            String.format("%.2f", (double) measurement.getTemperature()) + "," +
                            String.format("%.2f", (double) measurement.getPressure()) + "," +
                            String.format("%.2f", (double) measurement.getHumidity()) + "," +
                            String.format("%.2f", (double) measurement.getWind()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                if (!(marker.getSnippet() == null)) {
                    String[] data = marker.getSnippet().split(",");
                    if (data.length == 6) {
                        View window = getActivity().getLayoutInflater().inflate(R.layout.fragment_windoo_measured, null);
                        TextView time = (TextView) window.findViewById(R.id.time);
                        TextView time2 = (TextView) window.findViewById(R.id.time2);
                        TextView temperature = (TextView) window.findViewById(R.id.temperature);
                        TextView pressure = (TextView) window.findViewById(R.id.pressure);
                        TextView humidity = (TextView) window.findViewById(R.id.humidity);
                        TextView wind = (TextView) window.findViewById(R.id.wind);
                        time.setText(data[0]);
                        time2.setText(data[1]);
                        temperature.setText(data[2]);
                        pressure.setText(data[3]);
                        humidity.setText(data[4]);
                        wind.setText(data[5]);
                        return window;
                    }
                }
                return null;
            }
        });

        if (Wn2nacMap.goTo != null) {
            WindooMeasurement measurement = Wn2nacMap.goTo;
            LatLng pos = new LatLng(measurement.getLatitude(), measurement.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(String.valueOf(measurement.getSeq()))
                    .snippet(dateFormat.format(measurement.getCreatedAt()) + "," +
                            timeFormat.format(measurement.getCreatedAt()) + " ~ " + timeFormat.format(measurement.getUpdatedAt()) + "," +
                            String.format("%.2f", (double) measurement.getTemperature()) + "," +
                            String.format("%.2f", (double) measurement.getPressure()) + "," +
                            String.format("%.2f", (double) measurement.getHumidity()) + "," +
                            String.format("%.2f", (double) measurement.getWind()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            Wn2nacMap.goTo = null;
        }
    }

    public void onEventMainThread(WindooMeasureFragment.ShowEvent event) {
        getChildFragmentManager().beginTransaction().show(windooMeasureFragment).commit();
    }

    public void onEventMainThread(WindooMeasureFragment.HideEvent event) {
        getChildFragmentManager().beginTransaction().hide(windooMeasureFragment).commit();
    }

    public void onEventMainThread(Wn2nacMap.LocationUpdateEvent event) {
        userPos = new LatLng(Wn2nacMap.lastLocation.getLatitude(), Wn2nacMap.lastLocation.getLongitude());
        userMarker.setPosition(userPos);
    }

    public void onEventMainThread(Wn2nacMeasure.DoneEvent event) {
        LatLng pos = new LatLng(Wn2nacMeasure.measurement.getLatitude(), Wn2nacMeasure.measurement.getLongitude());
        Marker measurement = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(String.valueOf(Wn2nacMeasure.measurement.getSeq()))
                .snippet(dateFormat.format(Wn2nacMeasure.measurement.getCreatedAt()) + "," +
                                timeFormat.format(Wn2nacMeasure.measurement.getCreatedAt()) + " ~ " + timeFormat.format(Wn2nacMeasure.measurement.getUpdatedAt()) + "," +
                                String.format("%.2f", (double) Wn2nacMeasure.measurement.getTemperature()) + "," +
                                String.format("%.2f", (double) Wn2nacMeasure.measurement.getPressure()) + "," +
                                String.format("%.2f", (double) Wn2nacMeasure.measurement.getHumidity()) + "," +
                                String.format("%.2f", (double) Wn2nacMeasure.measurement.getWind()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        measurement.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }
}
