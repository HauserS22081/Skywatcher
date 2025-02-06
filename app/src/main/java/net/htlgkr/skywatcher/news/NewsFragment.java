package net.htlgkr.skywatcher.news;

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

import net.htlgkr.skywatcher.http.HttpListener;
import net.htlgkr.skywatcher.http.HttpViewModel;
import net.htlgkr.skywatcher.MainViewModel;
import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.details.DetailsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private int columnCount = 1;
    private MainViewModel mainViewModel;
    private FrameLayout loadingOverlay;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NewsViewModel newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        DetailsViewModel detailsViewModel = new ViewModelProvider(requireActivity()).get(DetailsViewModel.class);

        View tempview = inflater.inflate(R.layout.fragment_item_list, container, false);
        View view = tempview.findViewById(R.id.list);
        loadingOverlay = tempview.findViewById(R.id.fl_loadingOverlay);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }

            newsViewModel.observableNews.observe(getViewLifecycleOwner(), items -> {
                MyNewsRecyclerViewAdapter adapter = new MyNewsRecyclerViewAdapter(items);
                // oder 1 mal adapter initialisieren und in observe neue liste in methode in adapter mitgeben und dort NotifyDataSetChanged aufrufen
                recyclerView.setAdapter(adapter);
                adapter.setOnNewsClickListener(position -> {
                    detailsViewModel.setCurrentNews(items.get(position));
                    mainViewModel.showDetails();
                });
            });
        }

        if (detailsViewModel.getNews() == null || detailsViewModel.getNews().isEmpty()) {
            getNews();
        }
        return tempview;
    }

    private void getNews() {
        DetailsViewModel detailsViewModel = new ViewModelProvider(requireActivity()).get(DetailsViewModel.class);
        NewsViewModel newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);

        List<ExtendedNews> news = new ArrayList<>();
        HttpViewModel httpViewModel = new ViewModelProvider(requireActivity()).get(HttpViewModel.class);

        httpViewModel.requestDailyInfos(new HttpListener<ExtendedNews>() {
            @Override
            public void onSuccess(ExtendedNews response) {

                news.add(response);
                httpViewModel.requestSpaceNews(new HttpListener<List<ExtendedNews>>() {

                    @Override
                    public void onSuccess(List<ExtendedNews> response) {
                        for (ExtendedNews extendedNews : response) {
                            if (!news.contains(extendedNews)) {
                                news.add(extendedNews);
                            }
                        }

                        httpViewModel.requestSpaceTodaysNews(new HttpListener<List<ExtendedNews>>() {
                            @Override
                            public void onSuccess(List<ExtendedNews> response) {
                                for (ExtendedNews extendedNews : response) {
                                    if (!news.contains(extendedNews)) {
                                        news.add(extendedNews);
                                    }
                                }

                                httpViewModel.requestSpaceDayArticles(new HttpListener<List<ExtendedNews>>() {
                                    @Override
                                    public void onSuccess(List<ExtendedNews> response) {
                                        for (ExtendedNews extendedNews : response) {
                                            if (!news.contains(extendedNews)) {
                                                news.add(extendedNews);
                                            }
                                        }

                                        postToViewModel(news, detailsViewModel, newsViewModel);
                                        loadingOverlay.setVisibility(View.INVISIBLE);
                                    }
                                    @Override
                                    public void onError(String error) {
                                        Log.e("requestSpaceDayArticles", "requestSpaceDayArticles: " + ((error == null) ? "null" : error));
                                    }
                                });
                            }
                            @Override
                            public void onError(String error) {
                                Log.e("requestSpaceTodaysNews", "requestSpaceTodaysNews: " + ((error == null) ? "null" : error));
                            }
                        });
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("requestSpaceNews", "requestSpaceNews: " + ((error == null) ? "null" : error));
                    }
                });
            }
            @Override
            public void onError(String error) {
                Log.e("requestDailyInfos", "requestDailyInfos: " + ((error == null) ? "null" : error));
            }
        });
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void postToViewModel(List<ExtendedNews> news, DetailsViewModel detailsViewModel, NewsViewModel newsViewModel) {
        detailsViewModel.setNews(news);
        newsViewModel.addAll(news);
    }
}