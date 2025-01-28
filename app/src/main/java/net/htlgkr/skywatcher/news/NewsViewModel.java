package net.htlgkr.skywatcher.news;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.htlgkr.skywatcher.http.ExtendedNews;

import java.util.ArrayList;
import java.util.List;

public class NewsViewModel extends ViewModel {
    public MutableLiveData<ArrayList<ExtendedNews>> observableNews;
    private ArrayList<ExtendedNews> news;

    public NewsViewModel() {
        observableNews = new MutableLiveData<>();
        news = new ArrayList<>();
    }

    public void add(ExtendedNews news) {
        this.news.add(news);
        observableNews.postValue(this.news);
    }

    public void addAll(List<ExtendedNews> newsList) {
        this.news.addAll(newsList);
        observableNews.postValue(this.news);
    }
}
