package net.htlgkr.skywatcher.viewthesky;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.htlgkr.skywatcher.http.HttpListener;
import net.htlgkr.skywatcher.http.HttpViewModel;
import net.htlgkr.skywatcher.databinding.FragmentViewTheSkyBinding;
import net.htlgkr.skywatcher.viewthesky.helper.DeviceLocationHelper;
import net.htlgkr.skywatcher.viewthesky.helper.DeviceOrientationHelper;

import java.util.List;

public class ViewTheSkyFragment extends Fragment {

    private FragmentViewTheSkyBinding binding;
    private CanvasView canvasView;
    private ViewTheSkyViewModel viewTheSkyViewModel;
    private DeviceOrientationHelper deviceOrientationHelper;
    private FrameLayout loadingOverlay;

    public ViewTheSkyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewTheSkyBinding.inflate(inflater, container, false);

        canvasView = binding.canvasView;
        loadingOverlay = binding.flLoadingOverlay;

        viewTheSkyViewModel = new ViewModelProvider(requireActivity()).get(ViewTheSkyViewModel.class);

        DeviceLocationHelper deviceLocationHelper = new DeviceLocationHelper(requireContext());
        deviceLocationHelper.fetchLocation((location, timeMillis) -> {
            if (location != null) {
                viewTheSkyViewModel.setObserverLatitude(location.getLatitude());
                viewTheSkyViewModel.setObserverLongitude(location.getLongitude());
            } else {
                Log.e("Location", "Failed to get location.");
            }
        });

        deviceOrientationHelper = new DeviceOrientationHelper(requireContext(), (roll, pitch) -> {
            viewTheSkyViewModel.setPhoneRoll(roll);
            viewTheSkyViewModel.setPhonePitch(pitch);
            loadPlanets();
        });

        HttpViewModel httpViewModel = new ViewModelProvider(requireActivity()).get(HttpViewModel.class);
        httpViewModel.requestData(new HttpListener<List<Planet>>() {
            @Override
            public void onSuccess(List<Planet> response) {
                if (response == null) {
                    Log.e("requestData", "is null");
                } else if (response.isEmpty()) {
                    Log.e("requestData", "is empty");
                } else {
                    Log.e("requestData", "worked");

                    viewTheSkyViewModel.setPlanets(response);
                    loadingOverlay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("requestData", "Fehler beim Abrufen der Planeten: " + error);
            }
        }, ViewTheSkyViewModel.URL);

        loadingOverlay.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    private void loadPlanets() {
        boolean worked = viewTheSkyViewModel.loadPlanets();

        if (!worked) {
            return;
        }

        viewTheSkyViewModel.setPhoneHeight(canvasView.getHeight());
        viewTheSkyViewModel.setPhoneWidth(canvasView.getWidth());

        List<Planet> visiblePlanets = viewTheSkyViewModel.getVisiblePlanets();
        canvasView.updateVisiblePlanets(visiblePlanets);
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceOrientationHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        deviceOrientationHelper.stop();
    }

}