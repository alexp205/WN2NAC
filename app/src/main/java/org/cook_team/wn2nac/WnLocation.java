package org.cook_team.wn2nac;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import ch.skywatch.windoo.api.JDCWindooManager;
import de.greenrobot.event.EventBus;

public class WnLocation {

    private static final EventBus bus = EventBus.getDefault();

    /** WnLocation **/
    private static WnLocation instance = new WnLocation(); // Singleton instance
    private WnLocation(){
        if (!bus.isRegistered(this)) bus.register(this);
        locationManager = (LocationManager) WnApp.getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    /** LOCATION **/
    public static boolean locationEnabled = false;
    public static Location lastLocation;

    private static LocationManager locationManager;
    private static final LocationListener locationListenerNetwork = new MyLocationListener();
    private static final LocationListener locationListenerGPS = new MyLocationListener();

    /** Enable location **/
    public static class EnableEvent {}
    public void onEventMainThread(EnableEvent event)  { enable(); }
    public static void enable() {
        if (ActivityCompat.checkSelfPermission(WnApp.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            locationEnabled = true;
            bus.post(new WnService.DebugEvent("Location enabled"));
        } else {
            bus.post(new WnService.DebugEvent("Failed to enable location"));
        }
    }

    /** Disable location **/
    public static class DisableEvent {}
    public void onEventBackgroundThread(DisableEvent event) { disable(); }
    public static void disable() {
        if (ActivityCompat.checkSelfPermission(WnApp.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGPS);
            locationEnabled = false;
            bus.post(new WnService.DebugEvent("Location disabled"));
        } else {
            bus.post(new WnService.DebugEvent("Failed to disable location"));
        }
    }

    /** Fetch location **/
    public static class GetLastKnownLocationEvent {}
    public void onEventBackgroundThread(GetLastKnownLocationEvent event) { getLastKnownLocation(); }
    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(WnApp.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            bus.post(new WnService.DebugEvent("Last location fetched"));
        } else {
            bus.post(new WnService.DebugEvent("Failed to fetch last location"));
        }
    }

    /** Location updated **/
    public static class NewLocationEvent {}
    private static class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, lastLocation)) {
                Location loc = new Location(location.getProvider());
                loc.setAltitude(location.getAltitude());
                loc.setLatitude(location.getLatitude());
                loc.setLongitude(location.getLongitude());
                loc.setTime(location.getTime());
                loc.setAccuracy(location.getAccuracy());
                loc.setSpeed(location.getSpeed());
                loc.setBearing(location.getBearing());
                loc.setExtras(location.getExtras());
                //bus.post(new WnService.ToastEvent(String.valueOf(loc)));
                lastLocation = loc;
                bus.post(new NewLocationEvent());
                //bus.post(new DisableEvent());
                bus.post(new WnService.DebugEvent("New location: "
                        + String.valueOf(location.getLatitude()) + ", "
                        + String.valueOf(location.getLongitude()) + ", "
                        + location.getProvider()
                ));
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch(status) {
                case LocationProvider.AVAILABLE:
                    bus.post(new WnService.DebugEvent(provider + " AVAILABLE\n"));
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    bus.post(new WnService.DebugEvent(provider + " OUT_OF_SERVICE\n"));
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    bus.post(new WnService.DebugEvent(provider + " TEMPORARILY_UNAVAILABLE\n"));
                    break;
                default:
                    bus.post(new WnService.DebugEvent(provider + " " + String.valueOf(status)));
                    break;
            }
        }
        @Override
        public void onProviderEnabled(String provider) {
            bus.post(new WnService.DebugEvent("Location provider enabled:\n" + provider));
        }
        @Override
        public void onProviderDisabled(String provider) {
            bus.post(new WnService.DebugEvent("Location provider disabled:\n" + provider));
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {

        final int TWO_MINUTES = 1000 * 60 * 2;

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

}
