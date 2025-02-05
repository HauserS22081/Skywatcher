package net.htlgkr.skywatcher.viewthesky.helper;

import net.htlgkr.skywatcher.viewthesky.Planet;

public class PlanetPositionCalculator {

    private static final double PI = Math.PI;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    public static Planet calculateRAandDec(Planet planet, double observerLongitude) {
        double meanAnomaly = planet.getMainAnomaly();
        double eccentricity = planet.getEccentricity();
        double inclination = planet.getInclination();
        double argPeriapsis = planet.getArgPeriapsis();
        double longAscNode = planet.getLongAscNode();
        double semiMajorAxis = planet.getSemiMajorAxis();

        meanAnomaly = Math.toRadians(meanAnomaly);
        inclination = Math.toRadians(inclination);
        argPeriapsis = Math.toRadians(argPeriapsis);
        longAscNode = Math.toRadians(longAscNode);

        double eccentricAnomaly = solveKepler(meanAnomaly, eccentricity);

        double trueAnomaly = 2 * Math.atan2(
                Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2),
                Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2)
        );

        // distance r from the Sun
        double r = (semiMajorAxis * (1 - Math.pow(eccentricity, 2))) / (1 + eccentricity * Math.cos(trueAnomaly));

        double x = r * (Math.cos(longAscNode) * Math.cos(trueAnomaly + argPeriapsis) - Math.sin(longAscNode) * Math.sin(trueAnomaly + argPeriapsis) * Math.cos(inclination));
        double y = r * (Math.sin(longAscNode) * Math.cos(trueAnomaly + argPeriapsis) + Math.cos(longAscNode) * Math.sin(trueAnomaly + argPeriapsis) * Math.cos(inclination));
        double z = r * (Math.sin(trueAnomaly + argPeriapsis) * Math.sin(inclination));

        // x,y,z to equatorial coordinates (RA, Dec)
        double[] equatorialCoords = convertToEquatorial(x, y, z);

        double ra = equatorialCoords[0];
        double dec = equatorialCoords[1];

        double lst = calculateLocalSiderealTime(observerLongitude);
        ra = (ra + lst * 15) % 360;

        planet.setRa(ra);
        planet.setDec(dec);

        return planet;
    }

    public static double solveKepler(double meanAnomaly, double eccentricity) {
        double E = meanAnomaly;
        double tolerance = 1e-6;
        int maxIterations = 1000;

        for (int i = 0; i < maxIterations; i++) {
            double delta = E - eccentricity * Math.sin(E) - meanAnomaly;
            E = E - delta / (1 - eccentricity * Math.cos(E));
            if (Math.abs(delta) < tolerance) break;
        }

        return E;
    }

    public static double[] convertToEquatorial(double x, double y, double z) {
        double ra = Math.atan2(y, x);
        if (ra < 0) ra += 2 * PI;

        double dec = Math.asin(z / Math.sqrt(x * x + y * y + z * z));

        ra = ra * RAD_TO_DEG;
        dec = dec * RAD_TO_DEG;

        return new double[]{ra, dec};
    }

    public static double calculateLocalSiderealTime(double observerLongitude) {
        long currentMillis = System.currentTimeMillis();
        double jd = (currentMillis / 86400000.0) + 2440587.5; // Julian Date
        double d = jd - 2451545.0; // Days since J2000.0
        double gst = 18.697374558 + 24.06570982441908 * d; // Greenwich Sidereal Time
        return (gst + observerLongitude / 15.0) % 24; // Local Sidereal Time
    }
}

