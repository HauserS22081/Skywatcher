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

    private double phoneAzimuth = 0f;
    private double phonePitch = 0f;

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

        deviceOrientationHelper = new DeviceOrientationHelper(requireContext(), (azimuth, pitch) -> {
            phoneAzimuth = azimuth;
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
        canvasView.updateOrientation(phoneAzimuth, phonePitch);



        Log.e("loadPlanets", "visiblePlanets: " + visiblePlanets.size());
        Log.e("loadPlanets", "planets: " + planets.size());
        Log.d("loadPlanets", "Azimuth: "+ phoneAzimuth);
        Log.d("loadPlanets", "Pitch: "+ phonePitch);

        canvasView.updateVisiblePlanets(visiblePlanets);
    }

//    private List<Planet> calculatePlanetsForLocation() {
//        List<Planet> visiblePlanets = new ArrayList<>();
//
//        for (Planet planet : planets) {
//            // Nutze Standortdaten, um die Position des Planeten relativ zum Benutzer zu berechnen
//            //double azimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
//            double elevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);
//
//            // Füge Planeten hinzu, wenn er sichtbar ist
//            if (elevation > 0) { // Planet ist über dem Horizont
//                // planet.setAzimuth((float) azimuth);
//                planet.setElevation((float) elevation);
//                visiblePlanets.add(planet);
//            }
//        }
//
//        return visiblePlanets;
//    }

//    private List<Planet> calculatePlanetsForLocation() {
//
//        // hier berechnet man glaube ich ob über horizont sichtbar ist
//        // will ich aber nicht ich möchte auch planeten sehen wenn sie unter der erde quasi sind
//        //
//        // man soll den planeten einen wert geben der anzeigt in welcher position sie vom beobachter aus sind
//        // dann soll man berechnen mit orientation vom device welche bereiche gerade angeschaut werden
//        // die planten die sich in diese richtung befinden sollen zu visible planets hinzugefügt werden
//
//
//        List<Planet> visiblePlanets = new ArrayList<>();
//
//        for (Planet planet : planets) {
//            // Use location data (latitude, longitude) to calculate the planet's position
//            double elevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);
//
//            // We will also calculate the azimuth here if needed later
//            double azimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
//
//            // Only add planets that are visible (above the horizon)
//            if (elevation > 0) { // Planet is above the horizon
//                // Update the planet object with the azimuth and elevation
//                planet.setAzimuth((float) azimuth);
//                planet.setElevation((float) elevation);
//
//                // Add planet to the list of visible planets
//                visiblePlanets.add(planet);
//            }
//        }
//
//        // Optionally, update the planet's location or add any additional data for the canvas display
//        for (Planet planet : visiblePlanets) {
//            updatePlanetLocationForCanvas(planet);
//        }
//
//        return visiblePlanets;
//    }



    public static boolean isPlanetVisible(Planet planet, double deviceAzimuth, double devicePitch,
                                          double observerLatitude, double observerLongitude, double screenWidth, double screenHeight) {


        // map 3d to 2d coordinates



        // Konvertiere RA und DEC in Azimut und Elevation relativ zum Beobachter
        double planetAzimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
        double planetElevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);

        // Definiere eine Toleranz für Azimut und Elevation
        double azimuthTolerance = 30.0;  // +-30 Grad für den Azimut (erhöht für sanftere Übergänge)
        double elevationTolerance = 30.0;  // +-30 Grad für die Elevation (erhöht für sanftere Übergänge)

        // Berechne den Azimutbereich des Geräts
        double azimuthRangeStart = deviceAzimuth - azimuthTolerance;  // +-Toleranz nach links
        double azimuthRangeEnd = deviceAzimuth + azimuthTolerance;    // +-Toleranz nach rechts

        // Berechne den Pitchbereich des Geräts
        double pitchRangeStart = devicePitch - elevationTolerance;  // +-Toleranz nach unten
        double pitchRangeEnd = devicePitch + elevationTolerance;    // +-Toleranz nach oben

        // Passe Azimut an, um Werte zwischen 0 und 360 zu berücksichtigen
        if (azimuthRangeStart < 0) azimuthRangeStart += 360;
        if (azimuthRangeEnd >= 360) azimuthRangeEnd -= 360;

        // Überprüfe, ob der Planet im Bereich des Geräts liegt
        boolean isInAzimuthRange = (planetAzimuth >= azimuthRangeStart && planetAzimuth <= azimuthRangeEnd) ||
                (azimuthRangeStart > azimuthRangeEnd && (planetAzimuth >= azimuthRangeStart || planetAzimuth <= azimuthRangeEnd));
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
        return isInAzimuthRange && isInPitchRange && isPlanetInScreenBounds;
    }


    public static boolean isPlanetVisibleOld2(Planet planet, double deviceAzimuth, double devicePitch,
                                          double observerLatitude, double observerLongitude) {
        // Konvertiere RA und DEC in Azimut und Elevation relativ zum Beobachter
        double planetAzimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
        double planetElevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);

        // Definiere eine Toleranz für Azimut und Elevation
        double azimuthTolerance = 30.0;  // +-30 Grad für den Azimut (erhöht für sanftere Übergänge)
        double elevationTolerance = 30.0;  // +-30 Grad für die Elevation (erhöht für sanftere Übergänge)

        // Berechne den Azimutbereich des Geräts
        double azimuthRangeStart = deviceAzimuth - azimuthTolerance;  // +-Toleranz nach links
        double azimuthRangeEnd = deviceAzimuth + azimuthTolerance;    // +-Toleranz nach rechts

        // Berechne den Pitchbereich des Geräts
        double pitchRangeStart = devicePitch - elevationTolerance;  // +-Toleranz nach unten
        double pitchRangeEnd = devicePitch + elevationTolerance;    // +-Toleranz nach oben

        // Passe Azimut an, um Werte zwischen 0 und 360 zu berücksichtigen
        if (azimuthRangeStart < 0) azimuthRangeStart += 360;
        if (azimuthRangeEnd >= 360) azimuthRangeEnd -= 360;

        // Überprüfe, ob der Planet im Bereich des Geräts liegt
        boolean isInAzimuthRange = (planetAzimuth >= azimuthRangeStart && planetAzimuth <= azimuthRangeEnd) ||
                (azimuthRangeStart > azimuthRangeEnd && (planetAzimuth >= azimuthRangeStart || planetAzimuth <= azimuthRangeEnd));
        boolean isInPitchRange = planetElevation >= pitchRangeStart && planetElevation <= pitchRangeEnd;

        // Überprüfe, ob der Planet innerhalb der Toleranz des Azimuts und der Elevation liegt
        boolean isAzimuthInRange = Math.abs(planetAzimuth - deviceAzimuth) <= azimuthTolerance;
        boolean isElevationInRange = Math.abs(planetElevation - devicePitch) <= elevationTolerance;

        // Der Planet ist sichtbar, wenn er in beiden Bereichen liegt
        return isInAzimuthRange && isInPitchRange && isAzimuthInRange && isElevationInRange;
    }


    public static boolean isPlanetVisibleOLD(Planet planet, double deviceAzimuth, double devicePitch, double observerLatitude, double observerLongitude) {
        // Konvertiere RA und DEC in Azimut und Elevation relativ zum Beobachter
        double planetAzimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);
        double planetElevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);
        // -> kann man eigentlich 1 mal machen
        // Mit den Funktionen Converter.calculateAzimuthFromRADEC() und Converter.calculateElevationFromRADEC() werden die Rektaszension (RA) und Deklination (DEC) des Planeten relativ zum Beobachter (Benutzerstandort) in Azimut und Elevation umgerechnet.

        // Definiere eine Toleranz für Azimut und Elevation
        double azimuthTolerance = 10.0;  // +-10 Grad für den Azimut
        double elevationTolerance = 10.0;  // +-10 Grad für die Elevation

        // Berechne den Azimutbereich des Geräts
        double azimuthRangeStart = deviceAzimuth - 30;  // 30° nach links
        double azimuthRangeEnd = deviceAzimuth + 30;    // 30° nach rechts

        // Berechne den Pitchbereich des Geräts
        double pitchRangeStart = devicePitch - 15;  // 15° nach unten
        double pitchRangeEnd = devicePitch + 15;    // 15° nach oben

        // Passe Azimut an, um Werte zwischen 0 und 360 zu berücksichtigen
        if (azimuthRangeStart < 0) azimuthRangeStart += 360;
        if (azimuthRangeEnd >= 360) azimuthRangeEnd -= 360;

        // Überprüfe, ob der Planet im Bereich des Geräts liegt
        boolean isInAzimuthRange = (planetAzimuth >= azimuthRangeStart && planetAzimuth <= azimuthRangeEnd) ||
                (azimuthRangeStart > azimuthRangeEnd && (planetAzimuth >= azimuthRangeStart || planetAzimuth <= azimuthRangeEnd));
        boolean isInPitchRange = planetElevation >= pitchRangeStart && planetElevation <= pitchRangeEnd;

        // Überprüfe, ob der Planet innerhalb der Toleranz des Azimuts und der Elevation liegt
        boolean isAzimuthInRange = Math.abs(planetAzimuth - deviceAzimuth) <= azimuthTolerance;
        boolean isElevationInRange = Math.abs(planetElevation - devicePitch) <= elevationTolerance;

        return isInAzimuthRange && isInPitchRange && isAzimuthInRange && isElevationInRange;
    }

    private List<Planet> calculatePlanetsForLocation() {
        List<Planet> visiblePlanets = new ArrayList<>();

        for (Planet planet : planets) {

            if (isPlanetVisible(planet, phoneAzimuth, phonePitch, observerLatitude, observerLongitude, phoneWidth, phoneHeight)) {
                visiblePlanets.add(planet);
            }

        }

        return visiblePlanets;
    }

    public void updateVisiblePlanets(List<Planet> allPlanets,
                                     double phoneAzimuth, double phonePitch,
                                     double observerLatitude, double observerLongitude) {
        // Erstelle eine leere Liste für sichtbare Planeten
        List<Planet> visiblePlanets = new ArrayList<>();

        // Gehe durch alle Planeten und überprüfe, ob sie sichtbar sind
        for (Planet planet : allPlanets) {
            if (isPlanetVisible(planet, phoneAzimuth, phonePitch, observerLatitude, observerLongitude, phoneWidth, phoneHeight)) {
                visiblePlanets.add(planet);
            }
        }

        // Aktualisiere die globale Liste der sichtbaren Planeten
        this.visiblePlanets = visiblePlanets;
    }



    /*private List<Planet> calculatePlanetsForLocation() {

        // 1. Berechne für jeden Planeten die Position relativ zum Benutzer (Azimut und Elevation).
        // 2. Die Position zeigt an, wo der Planet vom Benutzerstandpunkt aus im Raum ist.
        // 3. Nutze die Azimut- und Pitch-Werte des Geräts, um zu überprüfen, ob der Planet in die Blickrichtung fällt.
        // 4. Wenn der Planet in der aktuellen Blickrichtung des Benutzers liegt, füge ihn der Liste der sichtbaren Planeten hinzu.
        // 5. Ignoriere nicht sichtbare Planeten, die außerhalb des vom Gerät abgedeckten Bereichs liegen.

        List<Planet> visiblePlanets = new ArrayList<>();

        for (Planet planet : planets) {
            // Berechne Azimut und Elevation relativ zum Benutzer
            double elevation = Converter.calculateElevation(planet, observerLatitude, observerLongitude);
            double azimuth = Converter.calculateAzimuth(planet, observerLatitude, observerLongitude);

            // Berechne den Bereich der sichtbaren Azimut- und Elevationswerte
            double azimuthRangeStart = phoneAzimuth - 30; // 30° nach links
            double azimuthRangeEnd = phoneAzimuth + 30;  // 30° nach rechts
            double pitchRangeStart = phonePitch - 15;    // 15° nach unten
            double pitchRangeEnd = phonePitch + 15;     // 15° nach oben

            // Passe Azimut an, um Werte zwischen 0 und 360 zu berücksichtigen
            if (azimuthRangeStart < 0) azimuthRangeStart += 360;
            if (azimuthRangeEnd >= 360) azimuthRangeEnd -= 360;

            // Prüfe, ob der Planet in den sichtbaren Bereich fällt
            boolean isInAzimuthRange = (azimuth >= azimuthRangeStart && azimuth <= azimuthRangeEnd) ||
                    (azimuthRangeStart > azimuthRangeEnd && (azimuth >= azimuthRangeStart || azimuth <= azimuthRangeEnd));
            boolean isInPitchRange = elevation >= pitchRangeStart && elevation <= pitchRangeEnd;

            if (isInAzimuthRange && isInPitchRange) {
                planet.setAzimuth((float) azimuth);
                planet.setElevation((float) elevation);
                visiblePlanets.add(planet);
            }
        }

        // Aktualisiere Planetenpositionen für die Anzeige auf dem Canvas
        for (Planet planet : visiblePlanets) {
            updatePlanetLocationForCanvas(planet);
        }

        return visiblePlanets;
    }*/


    // This method updates the planet's position and prepares it for display on a canvas (in your case, adding it as a bitmap)
    private void updatePlanetLocationForCanvas(Planet planet) {
        // Convert the planet's RA/Dec to screen coordinates for the canvas or use other parameters to calculate its position
        // Example: You could convert RA/Dec to x, y coordinates on the screen
        double ra = planet.getRa(); // Right Ascension in degrees
        double dec = planet.getDec(); // Declination in degrees

        // You might want to do some conversion here to map RA/Dec to canvas coordinates
        // For simplicity, let's assume you're using a basic conversion based on screen size
//        int canvasWidth = 1024; // Example canvas width
//        int canvasHeight = 1024; // Example canvas height
        int canvasWidth = canvasView.getWidth();
        int canvasHeight = canvasView.getHeight();

        // Example of RA/Dec to x, y conversion (simplified):
        int x = (int) ((ra / 360.0) * canvasWidth); // Map RA to x
        int y = (int) ((dec + 90.0) / 180.0 * canvasHeight); // Map Dec to y, adjust Dec range (-90 to +90)

        planet.setCanvasPositionX(x);
        planet.setCanvasPositionY(y);

        // You can also set a bitmap for the planet if needed
        // planet.setBitmap(bitmap);
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