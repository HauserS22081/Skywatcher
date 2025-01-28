package net.htlgkr.skywatcher.viewthesky.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;



public class DeviceLocationHelper {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private long currentTimeMillis;
    private Context context;

    public DeviceLocationHelper(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.context = context;
    }

    public void fetchLocation(OnLocationUpdatedListener listener) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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



