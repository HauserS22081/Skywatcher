package net.htlgkr.skywatcher.viewthesky;

import android.graphics.Bitmap;

public class Planet {

    private final String name;
    private final Bitmap bitmap;
    private final double mainAnomaly;
    private final double eccentricity;
    private final double inclination;
    private final double argPeriapsis;
    private final double longAscNode;
    private final double semiMajorAxis;
    private double ra;  // Right Ascension in degrees
    private double dec; // Declination in degrees

    private int canvasPositionX;
    private int canvasPositionY;

    private float azimuth;
    private float elevation;

    private float x;
    private float y;

    public Planet(String name, Bitmap bitmap, double mainAnomaly, double eccentricity, double inclination, double longAscNode, double argPeriapsis, double semiMajorAxis) {
        this.name = name;
        this.bitmap = bitmap;
        this.mainAnomaly = mainAnomaly;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longAscNode = longAscNode;
        this.argPeriapsis = argPeriapsis;
        this.semiMajorAxis = semiMajorAxis;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public String getName() {
        return name;
    }

    public double getMainAnomaly() {
        return mainAnomaly;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public double getInclination() {
        return inclination;
    }

    public double getArgPeriapsis() {
        return argPeriapsis;
    }

    public double getLongAscNode() {
        return longAscNode;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public double getRa() {
        return ra;
    }

    public void setRa(double ra) {
        this.ra = ra;
    }

    public double getDec() {
        return dec;
    }

    public void setDec(double dec) {
        this.dec = dec;
    }

}
