package net.htlgkr.skywatcher.skyobjectlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SkyObjectViewModel extends ViewModel {
    public MutableLiveData<ArrayList<SkyObject>> observableSkyObject;
    private ArrayList<SkyObject> skyObjects;

    public SkyObjectViewModel() {
        observableSkyObject = new MutableLiveData<>();
        skyObjects = new ArrayList<>();
    }

    public void addAll(ArrayList<SkyObject> skyObjects) {
        this.skyObjects = new ArrayList<>();
        this.skyObjects.addAll(skyObjects);
        observableSkyObject.postValue(this.skyObjects);
    }
}
