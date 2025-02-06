package net.htlgkr.skywatcher.skyobjectlist;

import androidx.lifecycle.ViewModel;

import net.htlgkr.skywatcher.R;

import java.util.ArrayList;

public class SkyObjectDataViewModel extends ViewModel {

    private ArrayList<SkyObject> skyObjects;
    private SkyObject currentSkyObject;

    public ArrayList<SkyObject> getSkyObjects() {
        return skyObjects;
    }

    public void setSkyObjects(ArrayList<SkyObject> skyObjects) {
        this.skyObjects = skyObjects;
    }

    public SkyObject getCurrentSkyObject() {
        return currentSkyObject;
    }

    public void setCurrentSkyObject(SkyObject currentSkyObject) {
        this.currentSkyObject = currentSkyObject;
    }

    public void mergeData(ArrayList<SkyObject> dataSkyObjects) {
        for (SkyObject dataSkyObject : dataSkyObjects) {


            for (SkyObject skyObject : skyObjects) {

                if (skyObject.getName().equalsIgnoreCase(dataSkyObject.getName())) {

                    skyObject.setAvgTemp(dataSkyObject.getAvgTemp());
                    skyObject.setGravity(dataSkyObject.getGravity());
                    skyObject.setMoonsCount(dataSkyObject.getMoonsCount());
                }
            }
        }
    }

    public void addValues() {
        skyObjects = new ArrayList<>();

        skyObjects.add(new SkyObject("Sun", R.drawable.sun, 4000));
        skyObjects.add(new SkyObject("Mercury", R.drawable.mercury, 300));
        skyObjects.add(new SkyObject("Venus", R.drawable.venus, 350));
        skyObjects.add(new SkyObject("Earth", R.drawable.earth, 350));
        skyObjects.add(new SkyObject("Mars", R.drawable.mars, 300));
        skyObjects.add(new SkyObject("Jupiter", R.drawable.jupiter, 700));
        skyObjects.add(new SkyObject("Saturn", R.drawable.saturn, 700));
        skyObjects.add(new SkyObject("Uranus", R.drawable.uranus, 400));
        skyObjects.add(new SkyObject("Neptune", R.drawable.neptune, 400));
    }
}
