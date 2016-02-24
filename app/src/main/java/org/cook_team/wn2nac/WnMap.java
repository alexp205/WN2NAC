package org.cook_team.wn2nac;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WnMap {

    private static final EventBus bus = EventBus.getDefault();

    /** CONSTANTS **/
    public static final LatLng NTUAS = new LatLng(25.014852, 121.538715);

    /** STATUS **/
    public static LatLng userPos = null;
    public static CameraPosition mCam;
    public static boolean init = false;
    public static boolean measureFragmentVisible = false;
    public static WindooMeasurement goTo;

    public static class GotoEvent {}
    public static class OpenEvent {}

    public List<MarkerOptions> Nx() {
        List<MarkerOptions> Nx = new ArrayList<>();
        Nx.add(new MarkerOptions().position(new LatLng(25.01233974854084,121.5377736702446)).title("N1"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01345756398008,121.5392943305315)).title("N2"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01511721762588,121.5413968137728)).title("N3"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01632874089701,121.5429035047102)).title("N4"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01751987785297,121.5439967099382)).title("N5"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01341801422619,121.5370684754793)).title("N6"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01458093268537,121.5386246658667)).title("N7"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01613184661733,121.5405656516363)).title("N8"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01735400200588,121.5419486579079)).title("N9"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01871796660154,121.5440468974173)).title("N10"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01462911863598,121.5356592237674)).title("N11"));
        Nx.add(new MarkerOptions().position(new LatLng(25.0155598678709,121.5377170953232)).title("N12"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01700595776638,121.5394231546164)).title("N13"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01833559011747,121.5415394002295)).title("N14"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01954391234505,121.5433138594165)).title("N15"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01530997904789,121.5351650535505)).title("N16"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01672392230912,121.5367714160253)).title("N17"));
        Nx.add(new MarkerOptions().position(new LatLng(25.0175686037313,121.5383238996631)).title("N18"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01924700183114,121.5403659083429)).title("N19"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02055896881219,121.5419449980864)).title("N20"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01639583983745,121.5337953714214)).title("N21"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01753262589489,121.5360545790262)).title("N22"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01873743810952,121.5373564758003)).title("N23"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02024626729829,121.5390452368388)).title("N24"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02104790499475,121.5402778128284)).title("N25"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01776900728526,121.5342040837808)).title("N26"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01928487973202,121.5356904564907)).title("N27"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02000671995009,121.5373913815537)).title("N28"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02107707488749,121.5385044002088)).title("N29"));
        Nx.add(new MarkerOptions().position(new LatLng(25.0195635095117,121.534118042228)).title("N30"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02083091443986,121.5360016407573)).title("N31"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02194143188726,121.5371727465063)).title("N32"));
        Nx.add(new MarkerOptions().position(new LatLng(25.02156543490328,121.5345509328406)).title("N33"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01974935878114,121.5349439451766)).title("N34"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01732485232453,121.5402054337283)).title("N35"));
        Nx.add(new MarkerOptions().position(new LatLng(25.01646044727832,121.5332881399327)).title("N36"));
        for (MarkerOptions n : Nx)
            n.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        return Nx;
    }

    public List<MarkerOptions> Dx() {
        List<MarkerOptions> Dx = new ArrayList<>();
        Dx.add(new MarkerOptions().position(new LatLng(25.03310062286578,121.5338865870907)).title("D1"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03294110021508,121.5355611262562)).title("D2"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03289244710232,121.5370920434978)).title("D3"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03178335117231,121.533822190301)).title("D4"));
        Dx.add(new MarkerOptions().position(new LatLng(25.0312226930442,121.5359755549965)).title("D5"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03087197623461,121.5372200920471)).title("D6"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02994459259724,121.5348600492632)).title("D7"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02992084715005,121.536418024184)).title("D8"));
        Dx.add(new MarkerOptions().position(new LatLng(25.0283937196088,121.5349422964844)).title("D9"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02842682179363,121.5364761942035)).title("D10"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02688114108198,121.535307529337)).title("D11"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02706640497706,121.536641649081)).title("D12"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02615424781948,121.5361918118563)).title("D13"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03295989690007,121.5321361958297)).title("D14"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03159401093689,121.5320243134184)).title("D15"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03014233089732,121.5322633247127)).title("D16"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03437896874777,121.5324899286839)).title("D17"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02741518732437,121.5330588157602)).title("D18"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02912404151194,121.5324907927369)).title("D19"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02602007935297,121.5337428431873)).title("D20"));
        Dx.add(new MarkerOptions().position(new LatLng(25.024620108741,121.5333893934279)).title("D21"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02432592392537,121.535854427946)).title("D22"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02540177226308,121.5373492317617)).title("D23"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02392119031327,121.5373048882843)).title("D24"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02391446747999,121.5394624388827)).title("D25"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02529043751498,121.539234824908)).title("D26"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02649926356152,121.5393878040673)).title("D27"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02790365513736,121.5393923087695)).title("D28"));
        Dx.add(new MarkerOptions().position(new LatLng(25.02901317219461,121.5393198383989)).title("D29"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03046860931054,121.5393455805874)).title("D30"));
        Dx.add(new MarkerOptions().position(new LatLng(25.0317297406103,121.539474148685)).title("D31"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03339125794537,121.5392552595016)).title("D32"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03422757857973,121.5395652054448)).title("D33"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03433411080918,121.5374971902277)).title("D34"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03408461833588,121.5357540861289)).title("D35"));
        Dx.add(new MarkerOptions().position(new LatLng(25.03418370879529,121.5336562258122)).title("D36"));
        for (MarkerOptions d : Dx)
            d.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        return Dx;
    }
}
