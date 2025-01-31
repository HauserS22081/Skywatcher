package net.htlgkr.skywatcher.viewthesky.helper;

import net.htlgkr.skywatcher.viewthesky.Planet;

public class Converter {

    // Berechnet die Elevation eines Planeten basierend auf den Koordinaten des Planeten und des Beobachtungsorts
//    public static double calculateElevation(Planet planet, double latitude, double longitude) {
//        // Umrechnung der Planetenlatitude und -longitude in Bogenmaß
//        double planetLatitudeRad = Math.toRadians(planet.getLatitude());
//        double planetLongitudeRad = Math.toRadians(planet.getLongitude());
//
//        // Umrechnung des Beobachtungsorts in Bogenmaß
//        double observerLatitudeRad = Math.toRadians(latitude);
//
//        // Berechnung der Elevation des Planeten
//        double elevation = Math.toDegrees(Math.asin(
//                Math.sin(planetLatitudeRad) * Math.sin(observerLatitudeRad) +
//                        Math.cos(planetLatitudeRad) * Math.cos(observerLatitudeRad) *
//                                Math.cos(planetLongitudeRad - Math.toRadians(longitude))
//        ));
//
//        return elevation;
//    }

        // Methode, um den Azimuth des Planeten zu berechnen
    public static float calculateAzimuth(Planet planet, double observerLatitude, double observerLongitude) {
        // Der Azimuth ist der Winkel entlang des Horizonts (Nord-Süd-Achse)
        // Der Azimuth wird normalerweise im Bereich 0° bis 360° gemessen, wobei 0° Norden und 90° Osten ist.

        double ra = Math.toRadians(planet.getRa()); // Umwandlung von RA in Bogenmaß
        double dec = Math.toRadians(planet.getDec()); // Umwandlung von Dec in Bogenmaß
        double lat = Math.toRadians(observerLatitude); // Umwandlung von Breite des Beobachters in Bogenmaß
        double lon = Math.toRadians(observerLongitude); // Umwandlung von Länge des Beobachters in Bogenmaß

        // Berechnung der Stunde des Beobachters in Stunden
        double lst = PlanetPositionCalculator.calculateLocalSiderealTime(observerLongitude);
        double lstRad = Math.toRadians(lst * 15); // Umwandlung der LST in Bogenmaß

        // Berechnung des Stundenwinkels (Hour Angle)
        double ha = lstRad - ra;
        if (ha < 0) ha += 2 * Math.PI; // Stelle sicher, dass der Stundenwinkel im Bereich von 0 bis 360° bleibt

        // Berechnung des Azimuths und der Höhe (Elevation) mit Hilfe der sphärischen trigonometrischen Formeln
        double x = Math.cos(ha) * Math.cos(dec);
        double y = Math.sin(ha) * Math.cos(dec);
        double z = Math.sin(dec);

        // Umrechnung in Horizontkoordinaten (Azimuth und Elevation)
        double azimuth = Math.atan2(y, x); // Azimuth berechnen
        double elevation = Math.asin(z); // Elevation berechnen

        // Azimuth in Grad umwandeln und sicherstellen, dass er im Bereich von 0 bis 360° bleibt
        azimuth = Math.toDegrees(azimuth);
        if (azimuth < 0) azimuth += 360;

        // Elevation in Grad umwandeln
        elevation = Math.toDegrees(elevation);

        return (float) azimuth;
    }

    // Methode, um die Elevation des Planeten zu berechnen
    public static float calculateElevation(Planet planet, double observerLatitude, double observerLongitude) {
        // Der Elevationswinkel gibt an, wie hoch der Planet am Himmel über dem Horizont ist
        double ra = Math.toRadians(planet.getRa()); // Umwandlung von RA in Bogenmaß
        double dec = Math.toRadians(planet.getDec()); // Umwandlung von Dec in Bogenmaß
        double lat = Math.toRadians(observerLatitude); // Umwandlung von Breite des Beobachters in Bogenmaß
        double lon = Math.toRadians(observerLongitude); // Umwandlung von Länge des Beobachters in Bogenmaß

        // Berechnung der Stunde des Beobachters in Stunden
        double lst = PlanetPositionCalculator.calculateLocalSiderealTime(observerLongitude);
        double lstRad = Math.toRadians(lst * 15); // Umwandlung der LST in Bogenmaß

        // Berechnung des Stundenwinkels (Hour Angle)
        double ha = lstRad - ra;
        if (ha < 0) ha += 2 * Math.PI;

        // Berechnung der Elevation
        double sinElevation = Math.sin(lat) * Math.sin(dec) + Math.cos(lat) * Math.cos(dec) * Math.cos(ha);
        double elevation = Math.asin(sinElevation); // Elevation berechnen in Bogenmaß
        elevation = Math.toDegrees(elevation); // Umwandlung der Elevation in Grad

        return (float) elevation;
    }


}
