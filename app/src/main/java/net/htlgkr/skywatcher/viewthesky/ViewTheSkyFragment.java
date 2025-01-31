package net.htlgkr.skywatcher.viewthesky;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import net.htlgkr.skywatcher.HttpListener;
import net.htlgkr.skywatcher.HttpViewModel;
import net.htlgkr.skywatcher.databinding.FragmentViewTheSkyBinding;
import net.htlgkr.skywatcher.viewthesky.helper.Converter;
import net.htlgkr.skywatcher.viewthesky.helper.DeviceLocationHelper;
import net.htlgkr.skywatcher.viewthesky.helper.DeviceOrientationHelper;
import net.htlgkr.skywatcher.viewthesky.helper.PlanetPositionCalculator;

import java.util.ArrayList;
import java.util.List;

public class ViewTheSkyFragment extends Fragment {

    private FragmentViewTheSkyBinding binding;

    private static final String URL = "https://api.le-systeme-solaire.net/rest/bodies";

    private double observerLatitude;
    private double observerLongitude;
    private double observerAltitude;

    //private double phoneAzimuth = 0f;
    private double phonePitch = 0f;
    private double phoneRoll = 0f;

    private CanvasView canvasView;
    private List<Planet> planets;
    private List<Planet> visiblePlanets;


    private long lastUpdateTime = 0;
    private DeviceOrientationHelper deviceOrientationHelper;

    private FrameLayout loadingOverlay;

    private double phoneHeight;
    private double phoneWidth;


    public ViewTheSkyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewTheSkyBinding.inflate(inflater, container, false);


        canvasView = binding.canvasView;
        loadingOverlay = binding.flLoadingOverlay;

        phoneHeight = canvasView.getHeight();
        phoneWidth = canvasView.getWidth();

        // deviceLocationHelper in succuess ausführen oder for über planets onsucess machen und device location helper davor machen
        DeviceLocationHelper deviceLocationHelper = new DeviceLocationHelper(requireContext());
        deviceLocationHelper.fetchLocation((location, timeMillis) -> {
            if (location != null) {
                // Get latitude, longitude, and altitude
                observerLatitude = location.getLatitude();
                observerLongitude = location.getLongitude();
                observerAltitude = location.getAltitude();

//                // Use the values
//                Log.d("Location", "Latitude: " + observerLatitude +
//                        ", Longitude: " + observerLongitude +
//                        ", Altitude: " + observerAltitude);

                loadPlanets();

            } else {
                Log.e("Location", "Failed to get location.");
            }
        });

        deviceOrientationHelper = new DeviceOrientationHelper(requireContext(), (roll, pitch) -> {
            phoneRoll = roll;
            phonePitch = pitch;



            loadPlanets();



//            // Optional: Zeige die Werte im Log
//            Log.e("Orientation", "Azimuth: " + phoneAzimuth + ", Pitch: " + phonePitch);
        });



        planets = new ArrayList<>();

        HttpViewModel httpViewModel = new ViewModelProvider(requireActivity()).get(HttpViewModel.class);
        httpViewModel.requestData(new HttpListener<List<Planet>>() {
            @Override
            public void onSuccess(List<Planet> response) {
                if (response == null) {
                    Log.e("requestData", "is null");
                } else if (response.isEmpty()) {
                    Log.e("requestData", "is empty");
                } else {
                    Log.e("requestData", "worked");
                    planets.addAll(response);

                    for (int i = 0; i < planets.size(); i++) {
                        Planet planet = PlanetPositionCalculator.calculateRAandDec(planets.get(i), observerLongitude);
                        planets.set(i, planet);
                    }

                    loadPlanets();

                    loadingOverlay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("requestData", "Fehler beim Abrufen der Planeten: " + error);
            }
        }, URL);

        loadingOverlay.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    private void loadPlanets() {

        // planeten in einem neuem ViewModel speichern

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 5) {
            return; // Abbrechen, wenn das letzte Update weniger als 1 Sekunde her ist
        }

        lastUpdateTime = currentTime;

        if (planets == null) {
            return;
        }

        visiblePlanets = calculatePlanetsForLocation();
        canvasView.updateOrientation(phoneRoll, phonePitch);



        Log.e("loadPlanets", "visiblePlanets: " + visiblePlanets.size());
        Log.e("loadPlanets", "planets: " + planets.size());
        Log.d("loadPlanets", "Roll: "+ phoneRoll);
        Log.d("loadPlanets", "Pitch: "+ phonePitch);

        canvasView.updateVisiblePlanets(visiblePlanets);
    }

    public static boolean isPlanetVisible(Planet planet, double deviceRoll, double devicePitch,
                                          double observerLatitude, double observerLongitude, double screenWidth, double screenHeight) {

        // map 3D to 2D coordinates

        // Konvertiere RA und DEC in Azimut und Elevation relativ zum Beobachter
        double planetAzimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
        double planetElevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);

        // Definiere eine Toleranz für Roll (anstelle von Azimut) und Elevation
        double rollTolerance = 30.0;  // +-30 Grad für den Roll (erhöht für sanftere Übergänge)
        double elevationTolerance = 30.0;  // +-30 Grad für die Elevation (erhöht für sanftere Übergänge)

        // Berechne den Rollbereich des Geräts
        double rollRangeStart = deviceRoll - rollTolerance;  // +-Toleranz nach links (oder roll)
        double rollRangeEnd = deviceRoll + rollTolerance;    // +-Toleranz nach rechts (oder roll)

        // Berechne den Pitchbereich des Geräts
        double pitchRangeStart = devicePitch - elevationTolerance;  // +-Toleranz nach unten
        double pitchRangeEnd = devicePitch + elevationTolerance;    // +-Toleranz nach oben

        // Passe Roll an, um Werte zwischen -180 und 180 zu berücksichtigen
        if (rollRangeStart < -180) rollRangeStart += 360;
        if (rollRangeEnd > 180) rollRangeEnd -= 360;

        // Überprüfe, ob der Planet im Bereich des Geräts liegt
        boolean isInRollRange = (planetAzimuth >= rollRangeStart && planetAzimuth <= rollRangeEnd) ||
                (rollRangeStart > rollRangeEnd && (planetAzimuth >= rollRangeStart || planetAzimuth <= rollRangeEnd));
        boolean isInPitchRange = planetElevation >= pitchRangeStart && planetElevation <= pitchRangeEnd;

        // Berechne den Azimut und die Elevation für die Bitmap-Darstellung
        double planetScreenX = (planetAzimuth / 360.0) * screenWidth;
        double planetScreenY = (planetElevation / 180.0) * screenHeight;

        // Berücksichtige den Randbereich, indem wir ein kleineres Bild des Planeten zulassen
        double screenMargin = 0.1 * Math.min(screenWidth, screenHeight);  // 10% vom Bildschirm als Randbereich

        // Überprüfe, ob der Planet innerhalb der Bildgrenzen liegt (auch im Randbereich)
        boolean isPlanetInScreenBounds = planetScreenX >= -screenMargin && planetScreenX <= screenWidth + screenMargin &&
                planetScreenY >= -screenMargin && planetScreenY <= screenHeight + screenMargin;

        // Der Planet ist sichtbar, wenn er in beiden Bereichen liegt und auch innerhalb der Bildgrenzen ist
        return isInRollRange && isInPitchRange && isPlanetInScreenBounds;
    }


    private List<Planet> calculatePlanetsForLocation() {
        List<Planet> visiblePlanets = new ArrayList<>();

        for (Planet planet : planets) {

            if (isPlanetVisible(planet, phoneRoll, phonePitch, observerLatitude, observerLongitude, phoneWidth, phoneHeight)) {
                visiblePlanets.add(planet);
            }

        }

        return visiblePlanets;
    }


    @Override
    public void onResume() {
        super.onResume();
        deviceOrientationHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        deviceOrientationHelper.stop();
    }

}