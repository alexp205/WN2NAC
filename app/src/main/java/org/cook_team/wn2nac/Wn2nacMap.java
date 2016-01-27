package org.cook_team.wn2nac;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.CameraPosition;

import de.greenrobot.event.EventBus;

public class Wn2nacMap {

    private static EventBus bus = EventBus.getDefault();
    public static CameraPosition mCam;
    public static boolean init = false;
    public static boolean measureFragmentVisible = false;
    public static int currentStep = 2;
    public static WindooMeasurement goTo;

    public Wn2nacMap() {
        if (!bus.isRegistered(this)) bus.register(this);
    }

    public static boolean locationEnabled = false;
    public static Location lastLocation;
    private static LocationManager locationManager;
    private static LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, lastLocation)) {
                lastLocation = location;
                bus.post(new LocationUpdateEvent());
            }
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    private static LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, lastLocation)) {
                lastLocation = location;
                bus.post(new LocationUpdateEvent());
            }
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);

            locationEnabled = true;
            bus.post(new LocationEnabledEvent());
        }
    }

    public void onEventMainThread(LocationDisableEvent event) {
        if (ActivityCompat.checkSelfPermission(Wn2nacService.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGPS);
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

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static class GotoEvent {}
    public static class OpenEvent {}
}
