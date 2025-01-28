package net.htlgkr.skywatcher.viewthesky.http;

public interface HttpListener<T> {
    void onSuccess(T response);

    void onError(String error);
}
