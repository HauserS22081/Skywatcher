package net.htlgkr.skywatcher.viewthesky;

import androidx.lifecycle.ViewModel;

public class ViewTheSkyViewModel extends ViewModel {
    private Planet planet;

    public Planet getPlanet() {
        return planet;
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }
}
