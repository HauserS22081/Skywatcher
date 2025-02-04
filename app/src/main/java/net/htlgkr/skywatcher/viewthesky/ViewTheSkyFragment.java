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

    private double phoneYaw = 0f;
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

        DeviceLocationHelper deviceLocationHelper = new DeviceLocationHelper(requireContext());
        deviceLocationHelper.fetchLocation((location, timeMillis) -> {
            if (location != null) {
                observerLatitude = location.getLatitude();
                observerLongitude = location.getLongitude();
                observerAltitude = location.getAltitude();
                loadPlanets();
            } else {
                Log.e("Location", "Failed to get location.");
            }
        });

        deviceOrientationHelper = new DeviceOrientationHelper(requireContext(), (yaw, pitch, roll) -> {
            phoneYaw = yaw;
            phoneRoll = roll;
            phonePitch = pitch;
            loadPlanets();
        });

        planets = new ArrayList<>();
        HttpViewModel httpViewModel = new ViewModelProvider(requireActivity()).get(HttpViewModel.class);
        httpViewModel.requestData(new HttpListener<List<Planet>>() {
            @Override
            public void onSuccess(List<Planet> response) {
                if (response != null && !response.isEmpty()) {
                    planets.addAll(response);

                    for (int i = 0; i < planets.size(); i++) {
                        Planet planet = PlanetPositionCalculator.calculateRAandDec(planets.get(i), observerLongitude);
                        planet.setAzimuth(Converter.calculateAzimuth(planet, observerLatitude, observerLongitude));
                        planet.setElevation(Converter.calculateElevation(planet, observerLatitude, observerLongitude));
                        planets.set(i, planet);
                    }

                    loadPlanets();
                    loadingOverlay.setVisibility(View.INVISIBLE);
                } else {
                    Log.e("requestData", "No planet data received.");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("requestData", "Error fetching planets: " + error);
            }
        }, URL);

        loadingOverlay.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    private void loadPlanets() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 5) {
            return;
        }

        lastUpdateTime = currentTime;

        if (planets == null) {
            return;
        }

        visiblePlanets = calculatePlanetsForLocation();
        canvasView.updateOrientation(phoneYaw, phonePitch, phoneRoll);
        canvasView.updateVisiblePlanets(visiblePlanets);

        Log.d("loadPlanets", "Yaw: " + phoneYaw);
        Log.d("loadPlanets", "Pitch: " + phonePitch);
        Log.d("loadPlanets", "Roll: " + phoneRoll);
        Log.d("loadPlanets", "Visible Planets: " + visiblePlanets.size());
    }

    public static boolean isPlanetVisible(Planet planet, double deviceYaw, double devicePitch, double deviceRoll,
                                          double observerLatitude, double observerLongitude, double screenWidth, double screenHeight) {

        double planetAzimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
        double planetElevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);

        double azimuthTolerance = 45.0;  // +-45° horizontale Sichtweite (hängt vom FOV der Kamera ab)
        double elevationTolerance = 30.0;  // +-30° vertikale Sichtweite

        double yawRangeStart = (deviceYaw - azimuthTolerance + 360) % 360;
        double yawRangeEnd = (deviceYaw + azimuthTolerance) % 360;
        double pitchRangeStart = devicePitch - elevationTolerance;
        double pitchRangeEnd = devicePitch + elevationTolerance;

        boolean isInYawRange = (planetAzimuth >= yawRangeStart && planetAzimuth <= yawRangeEnd) ||
                (yawRangeStart > yawRangeEnd && (planetAzimuth >= yawRangeStart || planetAzimuth <= yawRangeEnd));
        boolean isInPitchRange = planetElevation >= pitchRangeStart && planetElevation <= pitchRangeEnd;

        double screenX = (planetAzimuth / 360.0) * screenWidth;
        double screenY = ((90 - planetElevation) / 180.0) * screenHeight;

        double screenMargin = 0.1 * Math.min(screenWidth, screenHeight);

        boolean isInScreenBounds = screenX >= -screenMargin && screenX <= screenWidth + screenMargin &&
                screenY >= -screenMargin && screenY <= screenHeight + screenMargin;

        return isInYawRange && isInPitchRange && isInScreenBounds;
    }

    private List<Planet> calculatePlanetsForLocation() {
        List<Planet> visiblePlanets = new ArrayList<>();

        for (Planet planet : planets) {
            if (isPlanetVisible(planet, phoneYaw, phonePitch, phoneRoll, observerLatitude, observerLongitude, phoneWidth, phoneHeight)) {
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
