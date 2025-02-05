package net.htlgkr.skywatcher.viewthesky.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;



public class DeviceLocationHelper {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private long currentTimeMillis;
    private final Context context;

    public DeviceLocationHelper(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.context = context;
    }

    public void fetchLocation(OnLocationUpdatedListener listener) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    currentTimeMillis = System.currentTimeMillis();
                    listener.onLocationUpdated(currentLocation, currentTimeMillis);
                }
            }
        });
    }

    public interface OnLocationUpdatedListener {
        void onLocationUpdated(Location location, long timeMillis);
    }
}



