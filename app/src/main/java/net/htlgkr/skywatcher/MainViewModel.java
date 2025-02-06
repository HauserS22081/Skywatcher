package net.htlgkr.skywatcher;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public static final int START = 0;
    public static final int NEWS = 1;
    public static final int VIEWTHESKY = 2;
    public static final int DETAILS = 3;
    public static final int PLANETLIST = 4;

    private MutableLiveData<Integer> _state = new MutableLiveData<>(START);
    public LiveData<Integer> state = _state;

    public void showStart() {
        _state.postValue(START);
    }

    public void showNews() {
        _state.postValue(NEWS);
    }

    public void showViewTheSky() {
        _state.postValue(VIEWTHESKY);
    }

    public void showDetails() {
        _state.postValue(DETAILS);
    }

    public void showPlanetList() {
        _state.postValue(PLANETLIST);
    }
}
