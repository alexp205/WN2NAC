package org.cook_team.wn2nac;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import de.greenrobot.event.EventBus;

public class Wn2nacLocation {

    private static EventBus bus = EventBus.getDefault();

    public Wn2nacLocation() {
        if (!bus.isRegistered(this)) bus.register(this);
    }

    public static boolean locationEnabled = false;
    public static Location lastLocation;
    private static LocationManager locationManager;
    private static LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            lastLocation = location;
            bus.post(new LocationUpdateEvent());
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    public static class LocationEnableEvent {}
    public static class LocationDisableEvent {}
    public static class LocationFetchEvent {}
    public static class LocationEnabledEvent {}
    public static class LocationDisabledEvent {}
    public static class LocationFetchedEvent {}
    public static class LocationUpdateEvent {}
    public static class LocationDisplayEvent {
        public final Location location;
        public LocationDisplayEvent(Location location) {
            this.location = location;
        }
    }

    public static void init() {
        locationManager = (LocationManager) Wn2nacService.context.getSystemService(Context.LOCATION_SERVICE); // Acquire a reference to the system Location Manager
    }

    public void onEventMainThread(LocationEnableEvent event) {
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(Wn2nacService.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); // Or, use GPS location data
            locationEnabled = true;
            bus.post(new LocationEnabledEvent());
        }
    }

    public void onEventMainThread(LocationDisableEvent event) {
        if (ActivityCompat.checkSelfPermission(Wn2nacService.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
            locationEnabled = false;
            bus.post(new LocationDisabledEvent());
        }
    }

    public void onEventMainThread(LocationFetchEvent event) {
        if (ActivityCompat.checkSelfPermission(Wn2nacService.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            bus.post(new LocationFetchedEvent());
        }
    }
}
