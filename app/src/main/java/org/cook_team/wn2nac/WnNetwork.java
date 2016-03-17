package org.cook_team.wn2nac;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.skywatch.windoo.api.JDCWindooManager;
import de.greenrobot.event.EventBus;

public class WnNetwork {

    private static final EventBus bus = EventBus.getDefault();

    /** WnObserver **/
    private static WnNetwork instance = new WnNetwork(); // Singleton instance
    private WnNetwork(){
        if (!bus.isRegistered(this)) bus.register(this);
    }
    public WnNetwork getInstance() { return instance; }

    /** NETWORKING **/
    private static Cache cache;
    private static Network network;
    private static RequestQueue requestQueue;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Init networking **/
    public static void init() {
        cache = new DiskBasedCache(WnApp.getContext().getCacheDir(), 1024 * 1024); // Instantiate the cache (1MB cap)
        network = new BasicNetwork(new HurlStack()); // Set up the network to use HttpURLConnection as the HTTP client.
        requestQueue = new RequestQueue(cache, network); // Instantiate the RequestQueue with the cache and network.
        requestQueue.start(); // Start the queue
    }
    public static class InitEvent {}
    public void onEventBackgroundThread(InitEvent event) {
        init();
    }

    /** Send measurement **/
    public static class SendEvent {
        public final WindooMeasurement measurement;
        public SendEvent(WindooMeasurement measurement) {
            this.measurement = measurement;
        }
    }
    public void onEventBackgroundThread(final SendEvent event) {

        bus.post(new WnService.ToastEvent("傳送中..."));

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bus.post(new WnService.ToastEvent("量測資料傳送成功"));
                event.measurement.setTimeSent(System.currentTimeMillis());
                WnHistory.save(event.measurement);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bus.post(new WnService.ToastEvent("量測資料傳送失敗\n資料已儲存\n請稍後再傳送"));
               event.measurement.setTimeSent(-1);
                WnHistory.save(event.measurement);
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                WnApp.getContext().getString(R.string.default_server_address),
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id",              String.valueOf(event.measurement.getUserID()));
                map.put("windoo_id",            String.valueOf(event.measurement.getWindooID()));
                map.put("time_start",           dateFormat.format(event.measurement.getTimeStarted()));
                map.put("time_finish",          dateFormat.format(event.measurement.getTimeFinished()));
                map.put("location_latitude",    String.valueOf(event.measurement.getLastLatitude()));
                map.put("location_longitude",   String.valueOf(event.measurement.getLastLongitude()));
                map.put("location_altitude",    String.valueOf(event.measurement.getLastAltitude()));
                map.put("windoo_temperature",   String.valueOf(event.measurement.getTemperature()));
                map.put("windoo_humidity",      String.valueOf(event.measurement.getHumidity()));
                map.put("windoo_pressure",      String.valueOf(event.measurement.getPressure()));
                map.put("windoo_wind",          String.valueOf(event.measurement.getWind()));
                map.put("location_heading",     String.valueOf(event.measurement.getOrientation()));
                map.put("time_sent",            dateFormat.format(new Date()));
                return map;
            }
        };
        requestQueue.add(stringRequest);  // Add the request to the RequestQueue.
    }

}
