package net.htlgkr.skywatcher.viewthesky;

import android.graphics.Bitmap;

public class Planet {

//    public final String name;
//    public float azimuth;
//    public float elevation;
//    private double latitude;
//    private double longitude;
//    private double altitude;
//    public final Bitmap bitmap;
//
//    private double semiMajorAxis;  // Semi-major axis in km
//    private double eccentricity;   // Orbital eccentricity
//    private double inclination;    // Orbital inclination in degrees
//    private double longAscNode;    // Longitude of ascending node in degrees
//    private double argPeriapsis;   // Argument of periapsis in degrees
//    private double meanAnomaly;// Mean anomaly in degrees

    private String name;
    public final Bitmap bitmap;
    private double mainAnomaly;
    private double eccentricity;
    private double inclination;
    private double argPeriapsis;
    private double longAscNode;
    private double semiMajorAxis;
    private double ra;  // Right Ascension in degrees
    private double dec; // Declination in degrees

    private int canvasPositionX;
    private int canvasPositionY;

    private float azimuth;
    private float elevation;

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

    public int getCanvasPositionX() {
        return canvasPositionX;
    }

    public void setCanvasPositionX(int canvasPositionX) {
        this.canvasPositionX = canvasPositionX;
    }

    public int getCanvasPositionY() {
        return canvasPositionY;
    }

    public void setCanvasPositionY(int canvasPositionY) {
        this.canvasPositionY = canvasPositionY;
    }

    // Getters and setters for all properties
    public double getMainAnomaly() {
        return mainAnomaly;
    }

    public void setMainAnomaly(double mainAnomaly) {
        this.mainAnomaly = mainAnomaly;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public double getInclination() {
        return inclination;
    }

    public void setInclination(double inclination) {
        this.inclination = inclination;
    }

    public double getArgPeriapsis() {
        return argPeriapsis;
    }

    public void setArgPeriapsis(double argPeriapsis) {
        this.argPeriapsis = argPeriapsis;
    }

    public double getLongAscNode() {
        return longAscNode;
    }

    public void setLongAscNode(double longAscNode) {
        this.longAscNode = longAscNode;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public void setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
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
