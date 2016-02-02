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

import de.greenrobot.event.EventBus;

public class WnNetwork {

    private static final EventBus bus = EventBus.getDefault();

    /** NETWORKING **/
    public static Cache cache;
    public static Network network;
    public static RequestQueue requestQueue;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Init networking **/
    public static class InitEvent {}
    public static void init() {
        cache = new DiskBasedCache(WnService.context().getCacheDir(), 1024 * 1024); // Instantiate the cache (1MB cap)
        network = new BasicNetwork(new HurlStack()); // Set up the network to use HttpURLConnection as the HTTP client.
        requestQueue = new RequestQueue(cache, network); // Instantiate the RequestQueue with the cache and network.
        requestQueue.start(); // Start the queue
    }

    /** Send measurement **/
    public static class SendMeasurementEvent {
        public final WindooMeasurement measurement;
        public SendMeasurementEvent(WindooMeasurement measurement) {
            this.measurement = measurement;
        }
    }
    public static void send(final WindooMeasurement measurement) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                WnService.toast("量測資料傳送成功");
                measurement.setSentAt(new Date());
                WnHistory.save(measurement);
                WnHistory.read();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WnService.toast("量測資料傳送失敗\n資料已儲存\n請稍後再傳送");
            }
        };

        WnService.toast("傳送中...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                WnService.context().getResources().getString(R.string.default_server_address),
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(WnSettings.UserID));
                map.put("windoo_id", String.valueOf(WnSettings.WindooID));
                map.put("time_start", dateFormat.format(measurement.getCreatedAt()));
                map.put("time_finish", dateFormat.format(measurement.getUpdatedAt()));
                map.put("location_latitude", String.valueOf(measurement.getLatitude()));
                map.put("location_longitude", String.valueOf(measurement.getLongitude()));
                map.put("location_altitude", String.valueOf(measurement.getAltitude()));
                map.put("windoo_wind", String.valueOf(measurement.getWind()));
                map.put("windoo_temperature", String.valueOf(measurement.getTemperature()));
                map.put("windoo_humidity", String.valueOf(measurement.getHumidity()));
                map.put("windoo_pressure", String.valueOf(measurement.getPressure()));
                map.put("location_heading", String.valueOf(measurement.getOrientation()));
                map.put("time_sent", dateFormat.format(new Date()));
                return map;
            }
        };
        requestQueue.add(stringRequest);  // Add the request to the RequestQueue.
    }

}
