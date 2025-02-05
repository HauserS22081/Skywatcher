package net.htlgkr.skywatcher.viewthesky.helper;

import net.htlgkr.skywatcher.viewthesky.Planet;

public class Converter {

    public static float calculateAzimuth(Planet planet, double observerLatitude, double observerLongitude) {
        // Der Azimuth ist der Winkel entlang des Horizonts (Nord-Süd-Achse)
        // Bereich 0° - 360° (0°: Norden,90°: Osten)

        double ra = Math.toRadians(planet.getRa());
        double dec = Math.toRadians(planet.getDec());

        double lst = PlanetPositionCalculator.calculateLocalSiderealTime(observerLongitude);
        double lstRad = Math.toRadians(lst * 15);

        double ha = lstRad - ra;
        if (ha < 0) ha += 2 * Math.PI;

        double x = Math.cos(ha) * Math.cos(dec);
        double y = Math.sin(ha) * Math.cos(dec);
        double z = Math.sin(dec);

        double azimuth = Math.atan2(y, x);

        azimuth = Math.toDegrees(azimuth);
        if (azimuth < 0) azimuth += 360;

        return (float) azimuth;
    }

    public static float calculateElevation(Planet planet, double observerLatitude, double observerLongitude) {
        // wie hoch  Planet am Himmel über dem Horizont ist

        double ra = Math.toRadians(planet.getRa());
        double dec = Math.toRadians(planet.getDec());
        double lat = Math.toRadians(observerLatitude);

        double lst = PlanetPositionCalculator.calculateLocalSiderealTime(observerLongitude);
        double lstRad = Math.toRadians(lst * 15);

        double ha = lstRad - ra;
        if (ha < 0) ha += 2 * Math.PI;

        double sinElevation = Math.sin(lat) * Math.sin(dec) + Math.cos(lat) * Math.cos(dec) * Math.cos(ha);
        double elevation = Math.asin(sinElevation);
        elevation = Math.toDegrees(elevation);

        return (float) elevation;
    }
}
