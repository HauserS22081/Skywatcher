package net.htlgkr.skywatcher.viewthesky.helper;

import net.htlgkr.skywatcher.viewthesky.Planet;

public class PlanetPositionCalculator {

    // Constants
    private static final double PI = Math.PI;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    // Method to calculate RA and Dec based on orbital elements and update the planet object
    public static Planet calculateRAandDec(Planet planet, double observerLongitude) {
        double meanAnomaly = planet.getMainAnomaly(); // In degrees
        double eccentricity = planet.getEccentricity();
        double inclination = planet.getInclination(); // In degrees
        double argPeriapsis = planet.getArgPeriapsis(); // In degrees
        double longAscNode = planet.getLongAscNode(); // In degrees
        double semiMajorAxis = planet.getSemiMajorAxis(); // In km

        // Convert angles to radians
        meanAnomaly = Math.toRadians(meanAnomaly);
        inclination = Math.toRadians(inclination);
        argPeriapsis = Math.toRadians(argPeriapsis);
        longAscNode = Math.toRadians(longAscNode);

        // Solve Kepler's Equation to find Eccentric Anomaly (E)
        double eccentricAnomaly = solveKepler(meanAnomaly, eccentricity);

        // Calculate True Anomaly (ν)
        double trueAnomaly = 2 * Math.atan2(
                Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2),
                Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2)
        );

        // Calculate the distance r from the Sun
        double r = (semiMajorAxis * (1 - Math.pow(eccentricity, 2))) / (1 + eccentricity * Math.cos(trueAnomaly));

        // Calculate heliocentric coordinates (x, y, z)
        double x = r * (Math.cos(longAscNode) * Math.cos(trueAnomaly + argPeriapsis) - Math.sin(longAscNode) * Math.sin(trueAnomaly + argPeriapsis) * Math.cos(inclination));
        double y = r * (Math.sin(longAscNode) * Math.cos(trueAnomaly + argPeriapsis) + Math.cos(longAscNode) * Math.sin(trueAnomaly + argPeriapsis) * Math.cos(inclination));
        double z = r * (Math.sin(trueAnomaly + argPeriapsis) * Math.sin(inclination));

        // Convert heliocentric coordinates to equatorial coordinates (RA, Dec)
        double[] equatorialCoords = convertToEquatorial(x, y, z);

        double ra = equatorialCoords[0]; // Right Ascension in degrees
        double dec = equatorialCoords[1]; // Declination in degrees

        // Adjust for observer's longitude to get Local Sidereal Time (LST)
        double lst = calculateLocalSiderealTime(observerLongitude); // In hours
        ra = (ra + lst * 15) % 360; // Adjust RA based on LST (convert LST to degrees)

        // Set the RA and Dec values in the planet object
        planet.setRa(ra);
        planet.setDec(dec);

        // Return the updated planet object
        return planet;
    }

    // Solve Kepler's Equation to find the Eccentric Anomaly (E)
    public static double solveKepler(double meanAnomaly, double eccentricity) {
        double E = meanAnomaly; // Initial guess: E = M
        double tolerance = 1e-6; // Convergence tolerance
        int maxIterations = 1000;

        for (int i = 0; i < maxIterations; i++) {
            double delta = E - eccentricity * Math.sin(E) - meanAnomaly;
            E = E - delta / (1 - eccentricity * Math.cos(E));
            if (Math.abs(delta) < tolerance) break;
        }

        return E;
    }

    // Convert heliocentric (x, y, z) to equatorial (RA, Dec) coordinates
    public static double[] convertToEquatorial(double x, double y, double z) {
        // Right Ascension (RA)
        double ra = Math.atan2(y, x);
        if (ra < 0) ra += 2 * PI; // Ensure RA is positive

        // Declination (Dec)
        double dec = Math.asin(z / Math.sqrt(x * x + y * y + z * z));

        // Convert to degrees
        ra = ra * RAD_TO_DEG;
        dec = dec * RAD_TO_DEG;

        return new double[]{ra, dec};
    }

    // Calculate the Local Sidereal Time (LST) based on the observer's longitude
    public static double calculateLocalSiderealTime(double observerLongitude) {
        long currentMillis = System.currentTimeMillis();
        double jd = (currentMillis / 86400000.0) + 2440587.5; // Julian Date
        double d = jd - 2451545.0; // Days since J2000.0
        double gst = 18.697374558 + 24.06570982441908 * d; // Greenwich Sidereal Time (GST) in hours
        return (gst + observerLongitude / 15.0) % 24; // Local Sidereal Time (LST) in hours
    }
}

