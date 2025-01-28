package net.htlgkr.skywatcher.viewthesky.helper;

import android.annotation.SuppressLint;

import net.htlgkr.skywatcher.viewthesky.Planet;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PlanetVisibleHelper {
    // Calculate the azimuth and elevation of a planet based on the device's location
    public static double[] calculatePlanetDirection(Planet planet, double observerLat, double observerLon) {
        // Calculate the azimuth and elevation based on RA/Dec coordinates and location data
        double[] horizontal = convertToHorizontal(planet.getRa(), planet.getDec(), observerLat, observerLon);

        double altitude = horizontal[0]; // Elevation
        double azimuth = horizontal[1];  // Azimuth

        return new double[]{altitude, azimuth};
    }

    // Convert RA/Dec coordinates to horizontal coordinates
    public static double[] convertToHorizontal(double ra, double dec, double observerLat, double observerLon) {
        // Convert RA and Dec to horizontal coordinates
        double raRad = Math.toRadians(ra);
        double decRad = Math.toRadians(dec);
        double latRad = Math.toRadians(observerLat);
        double lonRad = Math.toRadians(observerLon);

        // Calculate Local Sidereal Time (LST)
        double lst = calculateLocalSiderealTime(observerLon);

        // Calculate the Hour Angle (HA)
        double hourAngle = lst - ra;

        // Calculate Elevation
        double elevation = Math.asin(
                Math.sin(decRad) * Math.sin(latRad) + Math.cos(decRad) * Math.cos(latRad) * Math.cos(hourAngle)
        );

        // Calculate Azimuth
        double azimuth = Math.atan2(
                -Math.cos(decRad) * Math.sin(hourAngle),
                Math.sin(decRad) - Math.sin(elevation) * Math.sin(latRad)
        );

        azimuth = (azimuth + 2 * Math.PI) % (2 * Math.PI); // Normalize to [0, 2π]

        // Convert to degrees
        elevation = Math.toDegrees(elevation);
        azimuth = Math.toDegrees(azimuth);

        return new double[]{elevation, azimuth};
    }

    // Calculate Local Sidereal Time (LST)
    public static double calculateLocalSiderealTime(double longitude) {
        // Calculate the local sidereal time based on geographic longitude
        long currentMillis = System.currentTimeMillis();
        double jd = (currentMillis / 86400000.0) + 2440587.5; // Julian Date
        double d = jd - 2451545.0; // Days since J2000.0
        double gst = 18.697374558 + 24.06570982441908 * d; // Greenwich Sidereal Time in hours
        return (gst + longitude / 15.0) % 24; // Local Sidereal Time in hours
    }

    // Julian Date calculator
    @SuppressLint("NewApi")
    public static double getJulianDate(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC) / 86400.0 + 2440587.5;
    }
}
