package org.cook_team.wn2nac;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacNetwork {

    private static EventBus bus = EventBus.getDefault();

    public static class ResponseReceivedEvent {
        public final String response;
        public ResponseReceivedEvent(String response) {
            this.response = response;
        }
    }
    public static class NetworkErrorEvent {
        public final VolleyError error;
        public NetworkErrorEvent(VolleyError error) {
            this.error = error;
        }
    }

    private static Cache cache;
    private static Network network;
    private static RequestQueue requestQueue;

    private static Response.Listener<String> responseListener;
    private static Response.ErrorListener errorListener;

    public static void init() {

        cache = new DiskBasedCache(Wn2nacService.context.getCacheDir(), 1024 * 1024); // Instantiate the cache (1MB ca)p
        network = new BasicNetwork(new HurlStack()); // Set up the network to use HttpURLConnection as the HTTP client.
        requestQueue = new RequestQueue(cache, network); // Instantiate the RequestQueue with the cache and network.
        requestQueue.start(); // Start the queue

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bus.post(new ResponseReceivedEvent(response));
                bus.post(new Wn2nacService.ToastEvent("量測資料傳送成功"));
                bus.post(new MeasurementSentEvent());
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bus.post(new NetworkErrorEvent(error));
                bus.post(new  Wn2nacService.ToastEvent("量測資料傳送失敗\n資料已儲存\n請稍後再傳送"));
            }
        };
    }

    public static String server_address;
    public static WindooMeasurement measurement;

    public static class SendMeasurementEvent {
        public final WindooMeasurement measurement;
        public SendMeasurementEvent(WindooMeasurement measurement) {
            this.measurement = measurement;
        }
    }
    public static void send() {
        bus.post(new Wn2nacService.ToastEvent("傳送中..."));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_address, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(Wn2nacPreferences.ID));
                map.put("windoo_id", String.valueOf(Wn2nacPreferences.WindooID));
                map.put("time_start", WindooEvent.dateFormat.format(measurement.getCreatedAt()));
                map.put("time_finish", WindooEvent.dateFormat.format(measurement.getUpdatedAt()));
                map.put("location_latitude", String.valueOf(measurement.getLatitude()));
                map.put("location_longitude", String.valueOf(measurement.getLongitude()));
                map.put("location_altitude", String.valueOf(measurement.getAltitude()));
                map.put("windoo_wind", String.valueOf(measurement.getWind()));
                map.put("windoo_temperature", String.valueOf(measurement.getTemperature()));
                map.put("windoo_humidity", String.valueOf(measurement.getHumidity()));
                map.put("windoo_pressure", String.valueOf(measurement.getPressure()));
                map.put("location_heading", String.valueOf(measurement.getOrientation()));
                map.put("time_sent", WindooEvent.dateFormat.format(new Date()));
                return map;
            }
        };
        requestQueue.add(stringRequest);  // Add the request to the RequestQueue.
    }

    public static class MeasurementSentEvent {}
}
