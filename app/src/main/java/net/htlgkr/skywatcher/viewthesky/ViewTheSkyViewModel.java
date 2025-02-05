package net.htlgkr.skywatcher.viewthesky;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import net.htlgkr.skywatcher.viewthesky.helper.Converter;
import net.htlgkr.skywatcher.viewthesky.helper.PlanetPositionCalculator;

import java.util.ArrayList;
import java.util.List;

public class ViewTheSkyViewModel extends ViewModel {
    public static final String URL = "https://api.le-systeme-solaire.net/rest/bodies";
    public static final double FOV_ELEVATION = 40.0;
    public static final double FOV_AZIMUTH = 60.0;
    private long lastUpdateTime;
    private double observerLatitude;
    private double observerLongitude;
    private double phonePitch;
    private double phoneRoll;
    private double phoneHeight;
    private double phoneWidth;

    private List<Planet> planets;
    private List<Planet> visiblePlanets;

    public boolean loadPlanets() {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 5) {
            return false;
        }
        lastUpdateTime = currentTime;

        if (planets == null) {
            return false;
        }

        Log.e("loadPlanets", "planets: " + planets.size());
        Log.d("loadPlanets", "Roll: "+ phoneRoll);
        Log.d("loadPlanets", "Pitch: "+ phonePitch);

        return true;
    }

    public void setPlanets(List<Planet> planets) {

        for (int i = 0; i < planets.size(); i++) {
            Planet planet = PlanetPositionCalculator.calculateRAandDec(planets.get(i), observerLongitude);
            planet.setAzimuth(Converter.calculateAzimuth(planet, observerLatitude, observerLongitude));
            planet.setElevation(Converter.calculateElevation(planet, observerLatitude, observerLongitude));
            planets.set(i, planet);
        }

        this.planets = planets;
    }

    public List<Planet> getVisiblePlanets() {
        calculateVisiblePlanets();
        return visiblePlanets;
    }

    private void calculateVisiblePlanets() {

        visiblePlanets = new ArrayList<>(planets);

        if (!visiblePlanets.isEmpty()) {
            double centerAzimuth = phonePitch;
            double centerElevation = phoneRoll;

            int idx = 0;

            for (int i = 0; i < planets.size(); i++) {

                Planet planet = visiblePlanets.get(idx);

                double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
                double relativeElevation = planet.getElevation() - centerElevation;

                // Normalisiere Azimut-Offset (-180 bis 180)
                while (relativeAzimuth > 180) relativeAzimuth -= 360;
                while (relativeAzimuth < -180) relativeAzimuth += 360;

                if (Math.abs(relativeAzimuth) > FOV_AZIMUTH / 2){
                    visiblePlanets.remove(planet);
                    continue;
                }
                if (Math.abs(relativeElevation) > FOV_ELEVATION / 2){
                    visiblePlanets.remove(planet);
                    continue;
                }

                planet.setX((float) (((relativeAzimuth + (FOV_AZIMUTH / 2)) / FOV_AZIMUTH) * phoneWidth));
                planet.setY((float) (((-(relativeElevation) + (FOV_ELEVATION / 2)) / FOV_ELEVATION) * phoneHeight));

                idx++;
            }
        }
    }

    public void setPhoneHeight(double phoneHeight) {
        this.phoneHeight = phoneHeight;
    }

    public void setPhoneWidth(double phoneWidth) {
        this.phoneWidth = phoneWidth;
    }

    public void setPhoneRoll(double phoneRoll) {
        this.phoneRoll = phoneRoll;
    }

    public void setPhonePitch(double phonePitch) {
        this.phonePitch = phonePitch;
    }

    public void setObserverLatitude(double observerLatitude) {
        this.observerLatitude = observerLatitude;
    }

    public void setObserverLongitude(double observerLongitude) {
        this.observerLongitude = observerLongitude;
    }
}
