package net.htlgkr.skywatcher;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import net.htlgkr.skywatcher.databinding.ActivityMainBinding;
import net.htlgkr.skywatcher.details.DetailsFragment;
import net.htlgkr.skywatcher.details.DetailsViewModel;
import net.htlgkr.skywatcher.http.HttpViewModel;
import net.htlgkr.skywatcher.news.NewsFragment;
import net.htlgkr.skywatcher.news.NewsViewModel;
import net.htlgkr.skywatcher.skyobjectlist.SkyObjectDataViewModel;
import net.htlgkr.skywatcher.skyobjectlist.SkyObjectFragment;
import net.htlgkr.skywatcher.skyobjectlist.SkyObjectViewModel;
import net.htlgkr.skywatcher.start.StartFragment;
import net.htlgkr.skywatcher.viewthesky.ViewTheSkyFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        requestLocationPermission();

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        NewsViewModel newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        SkyObjectViewModel skyObjectViewModel = new ViewModelProvider(this).get(SkyObjectViewModel.class);
        DetailsViewModel detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        SkyObjectDataViewModel skyObjectDataViewModel = new ViewModelProvider(this).get(SkyObjectDataViewModel.class);
        HttpViewModel httpViewModel = new ViewModelProvider(this).get(HttpViewModel.class);
        httpViewModel.init(getApplicationContext());


        mainViewModel.state.observe(this, state -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch (state) {
                case MainViewModel.START: {
                    fragmentTransaction.replace(R.id.main, new StartFragment(), "START FRAGMENT");
                    break;
                }
                case MainViewModel.NEWS: {
                    fragmentTransaction.replace(R.id.main, new NewsFragment(), "NEWS FRAGMENT");
                    fragmentTransaction.addToBackStack("ADDED START TO BACKSTACK");
                    break;
                }
                case MainViewModel.VIEWTHESKY: {
                    fragmentTransaction.replace(R.id.main, new ViewTheSkyFragment(), "VIEWTHESKY FRAGMENT"); // hier gehört richtiges fragment hin
                    fragmentTransaction.addToBackStack("AND START TO BACKSTACK");
                    break;
                }
                case MainViewModel.DETAILS: {
                    fragmentTransaction.replace(R.id.main, new DetailsFragment(), "DETAILS FRAGMENT");
                    fragmentTransaction.addToBackStack("AND NEWS TO BACKSTACK");
                    break;
                }
                case MainViewModel.PLANETLIST: {
                    fragmentTransaction.replace(R.id.main, new SkyObjectFragment(), "");
                    fragmentTransaction.addToBackStack("");
                }

            }

            fragmentTransaction.commit();
        });

    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}