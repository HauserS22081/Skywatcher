package net.htlgkr.skywatcher.skyobjectlist;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.http.HttpListener;
import net.htlgkr.skywatcher.http.HttpViewModel;

import java.util.ArrayList;


public class SkyObjectFragment extends Fragment {

    private int columnCount = 1;
    private SkyObjectDataViewModel skyObjectDataViewModel;
    private FrameLayout loadingOverlay;

    public SkyObjectFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sky_object_list, container, false);

        SkyObjectViewModel viewModel = new ViewModelProvider(requireActivity()).get(SkyObjectViewModel.class);
        skyObjectDataViewModel = new ViewModelProvider(requireActivity()).get(SkyObjectDataViewModel.class);
        MyBottomSheetFragment bottomSheet = new MyBottomSheetFragment();

        loadingOverlay = view.findViewById(R.id.fl_loadingOverlay);

        View tempView = view.findViewById(R.id.rv_list);

        if (tempView instanceof RecyclerView) {
            Context context = tempView.getContext();
            RecyclerView recyclerView = (RecyclerView) tempView;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }

            viewModel.observableSkyObject.observe(getViewLifecycleOwner(), items -> {

                Log.d("Observer", "SkyObjects updated, new size: " + (items != null ? items.size() : 0));

                MySkyObjectRecyclerViewAdapter adapter = new MySkyObjectRecyclerViewAdapter(items);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(position -> {
                    assert items != null;
                    SkyObject skyObject = items.get(position);

                    if ("Sun".equalsIgnoreCase(skyObject.getName())) {
                        return;
                    }

                    skyObjectDataViewModel.setCurrentSkyObject(skyObject);
                    bottomSheet.show(getChildFragmentManager(), "MyBottomSheet");
                });
            });
        }

        skyObjectDataViewModel.addValues();
        getAndMergeData();
        loadingOverlay.setVisibility(View.VISIBLE);

        return view;
    }

    private void getAndMergeData() {
        HttpViewModel httpViewModel = new ViewModelProvider(requireActivity()).get(HttpViewModel.class);

        httpViewModel.requestPlanetInfo(new HttpListener<ArrayList<SkyObject>>() {
            @Override
            public void onSuccess(ArrayList<SkyObject> response) {
                skyObjectDataViewModel.mergeData(response);

                ArrayList<SkyObject> skyObjects = skyObjectDataViewModel.getSkyObjects();
                SkyObjectViewModel viewModel = new ViewModelProvider(requireActivity()).get(SkyObjectViewModel.class);

                viewModel.addAll(skyObjects);

                loadingOverlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {
                Log.e("requestPlanetInfo", "requestPlanetInfo: " + ((error == null) ? "null" : error));
            }
        });
    }
}