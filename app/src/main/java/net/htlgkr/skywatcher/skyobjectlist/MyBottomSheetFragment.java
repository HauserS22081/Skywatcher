package net.htlgkr.skywatcher.skyobjectlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.htlgkr.skywatcher.databinding.FragmentBottomSheetBinding;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        net.htlgkr.skywatcher.databinding.FragmentBottomSheetBinding binding = FragmentBottomSheetBinding.inflate(inflater, container, false);

        SkyObjectDataViewModel viewModel = new ViewModelProvider(requireActivity()).get(SkyObjectDataViewModel.class);
        SkyObject skyObject = viewModel.getCurrentSkyObject();

        binding.tvName.setText(skyObject.getName());
        binding.tvMoonCount.setText("Moon Count: " + skyObject.getMoonsCount());
        binding.tvAvgTemp.setText(String.format("Average Tempertature: %.2f °C", skyObject.getAvgTemp()));
        binding.tvGravity.setText(String.format("Gravity: %.2f",skyObject.getGravity()));

        return binding.getRoot();
    }
}

