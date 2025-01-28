package net.htlgkr.skywatcher.start;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.htlgkr.skywatcher.MainViewModel;
import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.databinding.FragmentStartBinding;


public class StartFragment extends Fragment {

    private FragmentStartBinding binding;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartBinding.inflate(inflater, container, false);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.cvNews.setOnClickListener(view -> {
            mainViewModel.showNews();
        });

        binding.cvViewTheSky.setOnClickListener(view -> {
            mainViewModel.showViewTheSky();
        });

        return binding.getRoot();
    }
}