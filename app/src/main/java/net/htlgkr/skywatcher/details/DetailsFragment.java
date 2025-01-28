package net.htlgkr.skywatcher.details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.htlgkr.skywatcher.MainViewModel;
import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.databinding.FragmentDetailsBinding;
import net.htlgkr.skywatcher.http.ExtendedNews;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        DetailsViewModel detailsViewModel = new ViewModelProvider(requireActivity()).get(DetailsViewModel.class);

        ExtendedNews currentNews = detailsViewModel.getCurrentNews();
        if (currentNews != null) {

            binding.tvTitle.setText(currentNews.getTitle());
            binding.tvSubtitle.setText(currentNews.getSubtitle());
            binding.tvDescription.setText(currentNews.getDescription());
            binding.tvDescription.setLineSpacing(0, 0.9f);

        }

        return binding.getRoot();
    }
}