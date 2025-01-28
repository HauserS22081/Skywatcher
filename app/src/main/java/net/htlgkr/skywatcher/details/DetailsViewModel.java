package net.htlgkr.skywatcher.details;

import androidx.lifecycle.ViewModel;

import net.htlgkr.skywatcher.http.ExtendedNews;

import java.util.List;

public class DetailsViewModel extends ViewModel {
    private List<ExtendedNews> news;
    private ExtendedNews currentNews;

    public void setNews(List<ExtendedNews> news) {
        this.news = news;
    }

    public void setCurrentNews(ExtendedNews currentNews) {
        this.currentNews = currentNews;
    }

    public ExtendedNews getCurrentNews() {
        return currentNews;
    }

    public List<ExtendedNews> getNews() {
        return news;
    }
}
