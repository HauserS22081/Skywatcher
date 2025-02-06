package net.htlgkr.skywatcher.skyobjectlist;

public class SkyObject {

    private int image;
    private int size;
    private String name;
    private double gravity;
    private double avgTemp;
    private int moonsCount;

    public SkyObject(String name, int image, int size) {
        this.name = name;
        this.image = image;
        this.size = size;
    }


    public SkyObject(String name, double gravity, double avgTemp, int moonsCount) {
        this.name = name;
        this.gravity = gravity;
        this.avgTemp = avgTemp;
        this.moonsCount = moonsCount;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public void setMoonsCount(int moonsCount) {
        this.moonsCount = moonsCount;
    }

    public double getGravity() {
        return gravity;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public int getMoonsCount() {
        return moonsCount;
    }
}
