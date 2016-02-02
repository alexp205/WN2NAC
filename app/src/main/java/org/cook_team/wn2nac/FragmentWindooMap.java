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

public class FragmentWindooMap extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    private static EventBus bus = EventBus.getDefault();
    FragmentWindooMeasure fragmentWindooMeasure;
    FragmentWindooMapMenu fragmentWindooMapMenu;
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
        bus.post(new WnLocation.EnableLocationEvent());
        bus.post(new WnMeasurement.UpdateEvent());

    }

    @Override
    public void onPause() {
        WnMap.mCam = mMap.getCameraPosition();
        bus.post(new WnLocation.DisableLocationEvent());
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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bus.post(new WnLocation.EnableLocationEvent());

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

        if (!WnMap.init) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(NTUAS));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

            if (WnLocation.lastLocation != null) {
                userPos = new LatLng(WnLocation.lastLocation.getLatitude(), WnLocation.lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }

            WnMap.mCam = mMap.getCameraPosition();
            WnMap.init = true;
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(WnMap.mCam));
        }

        /*userMarker = mMap.addMarker(new MarkerOptions()
                .position(userPos)
                .title("使用者")
                .snippet("ID: 0"));*/

        /** MARKERS **/
        List<Marker> Dx = new ArrayList<>();
        List<Marker> Nx = new ArrayList<>();
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03310062286578,121.5338865870907)).title("D1")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03294110021508,121.5355611262562)).title("D2")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03289244710232,121.5370920434978)).title("D3")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03178335117231,121.533822190301)).title("D4")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0312226930442,121.5359755549965)).title("D5")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03087197623461,121.5372200920471)).title("D6")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02994459259724,121.5348600492632)).title("D7")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02992084715005,121.536418024184)).title("D8")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0283937196088,121.5349422964844)).title("D9")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02842682179363,121.5364761942035)).title("D10")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02688114108198,121.535307529337)).title("D11")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02706640497706,121.536641649081)).title("D12")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02615424781948,121.5361918118563)).title("D13")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03295989690007,121.5321361958297)).title("D14")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03159401093689,121.5320243134184)).title("D15")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03014233089732,121.5322633247127)).title("D16")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03437896874777,121.5324899286839)).title("D17")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02741518732437,121.5330588157602)).title("D18")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02912404151194,121.5324907927369)).title("D19")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02602007935297,121.5337428431873)).title("D20")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.024620108741,121.5333893934279)).title("D21")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02432592392537,121.535854427946)).title("D22")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02540177226308,121.5373492317617)).title("D23")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02392119031327,121.5373048882843)).title("D24")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02391446747999,121.5394624388827)).title("D25")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02529043751498,121.539234824908)).title("D26")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02649926356152,121.5393878040673)).title("D27")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02790365513736,121.5393923087695)).title("D28")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02901317219461,121.5393198383989)).title("D29")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03046860931054,121.5393455805874)).title("D30")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0317297406103,121.539474148685)).title("D31")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03339125794537,121.5392552595016)).title("D32")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03422757857973,121.5395652054448)).title("D33")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03433411080918,121.5374971902277)).title("D34")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03408461833588,121.5357540861289)).title("D35")));
        Dx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.03418370879529,121.5336562258122)).title("D36")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01233974854084,121.5377736702446)).title("N1")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01345756398008,121.5392943305315)).title("N2")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01511721762588,121.5413968137728)).title("N3")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01632874089701,121.5429035047102)).title("N4")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01751987785297,121.5439967099382)).title("N5")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01341801422619,121.5370684754793)).title("N6")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01458093268537,121.5386246658667)).title("N7")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01613184661733,121.5405656516363)).title("N8")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01735400200588,121.5419486579079)).title("N9")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01871796660154,121.5440468974173)).title("N10")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01462911863598,121.5356592237674)).title("N11")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0155598678709,121.5377170953232)).title("N12")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01700595776638,121.5394231546164)).title("N13")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01833559011747,121.5415394002295)).title("N14")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01954391234505,121.5433138594165)).title("N15")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01530997904789,121.5351650535505)).title("N16")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01672392230912,121.5367714160253)).title("N17")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0175686037313,121.5383238996631)).title("N18")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01924700183114,121.5403659083429)).title("N19")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02055896881219,121.5419449980864)).title("N20")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01639583983745,121.5337953714214)).title("N21")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01753262589489,121.5360545790262)).title("N22")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01873743810952,121.5373564758003)).title("N23")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02024626729829,121.5390452368388)).title("N24")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02104790499475,121.5402778128284)).title("N25")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01776900728526,121.5342040837808)).title("N26")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01928487973202,121.5356904564907)).title("N27")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02000671995009,121.5373913815537)).title("N28")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02107707488749,121.5385044002088)).title("N29")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.0195635095117,121.534118042228)).title("N30")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02083091443986,121.5360016407573)).title("N31")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02194143188726,121.5371727465063)).title("N32")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.02156543490328,121.5345509328406)).title("N33")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01974935878114,121.5349439451766)).title("N34")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01732485232453,121.5402054337283)).title("N35")));
        Nx.add(mMap.addMarker(new MarkerOptions().position(new LatLng(25.01646044727832,121.5332881399327)).title("N36")));
        for (Marker d : Dx) {
            d.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            d.showInfoWindow();
        }
        for (Marker n : Nx) {
            n.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            n.showInfoWindow();
        }
        for (WindooMeasurement measurement : WnHistory.history) {
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

        if (WnMap.goTo != null) {
            WindooMeasurement measurement = WnMap.goTo;
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
            WnMap.goTo = null;
        }
    }

    public void onEventMainThread(FragmentWindooMeasure.ShowEvent event) {
        getChildFragmentManager().beginTransaction().show(fragmentWindooMeasure).commit();
    }

    public void onEventMainThread(FragmentWindooMeasure.HideEvent event) {
        getChildFragmentManager().beginTransaction().hide(fragmentWindooMeasure).commit();
    }

    public void onEventMainThread(WnLocation.LocationUpdatedEvent event) {
        userPos = new LatLng(WnLocation.lastLocation.getLatitude(), WnLocation.lastLocation.getLongitude());
        //userMarker.setPosition(userPos);
    }

    public void onEventMainThread(WnMeasurement.DoneEvent event) {
        WindooMeasurement measurement = event.measurement;
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
    }
}
