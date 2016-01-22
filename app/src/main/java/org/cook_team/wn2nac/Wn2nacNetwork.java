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

import java.util.HashMap;
import java.util.Map;

import ch.skywatch.windoo.api.JDCWindooMeasurement;
import de.greenrobot.event.EventBus;

public class Wn2nacNetwork {

    private static EventBus bus = EventBus.getDefault();

    public Wn2nacNetwork() {
        if (!bus.isRegistered(this)) bus.register(this);
    }

    public static String server_address;

    public static class CheckNetworkEvent {}
    public static class NetworkAvailableEvent {}
    public static class NetworkNotAvailableEvent {}
    public static class MeasurementSentEvent {}
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
    public static class SendMeasurementEvent {
        public final int no;
        public SendMeasurementEvent(int no) {
            this.no = no;
        }
    }
    public static class NetworkEvent {
        public final String message;
        public NetworkEvent(String message) {
            this.message = message;
        }
    }

    private static Cache cache;
    private static Network network;
    private static RequestQueue requestQueue;

    private static Response.Listener<String> responseListener;
    private static Response.ErrorListener errorListener;

    public static void init() {

        // Instantiate the cache
        cache = new DiskBasedCache(Wn2nacService.context.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bus.post(new ResponseReceivedEvent(response));
                bus.post(new NetworkEvent("傳送成功"));
                bus.post(new MeasurementSentEvent());
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bus.post(new NetworkErrorEvent(error));
                bus.post(new NetworkEvent("傳送失敗\n請再試一次\n" + error.toString() + "\n" + error.getCause() + "\n" + error.getMessage() + "\n" + error.getStackTrace()));
            }
        };
    }

    public void onEventAsync(CheckNetworkEvent event) {
        ConnectivityManager connMgr = (ConnectivityManager)
                Wn2nacService.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            bus.post(new NetworkAvailableEvent());
        } else {
            bus.post(new NetworkNotAvailableEvent());
        }
    }

    public void onEventAsync(final SendMeasurementEvent event)  {

        bus.post(new NetworkEvent("傳送中..."));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_address, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                WindooMeasurement measurement = Wn2nacHistory.measurement.get(event.no);
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", String.valueOf(measurement.getNickname()));
                map.put("time_start", WindooEvent.dateFormat.format(measurement.getCreatedAt()));
                map.put("time_finish", WindooEvent.dateFormat.format(measurement.getUpdatedAt()));
                map.put("location_latitude", String.valueOf(measurement.getLatitude()));
                map.put("location_longitude", String.valueOf(measurement.getLongitude()));
                map.put("location_altitude", String.valueOf(measurement.getAltitude()));
                map.put("windoo_wind", String.valueOf(measurement.getWind()));
                map.put("windoo_temperature", String.valueOf(measurement.getTemperature()));
                map.put("windoo_humidity", String.valueOf(measurement.getHumidity()));
                map.put("windoo_pressure", String.valueOf(measurement.getPressure()));
                return map;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }
}
