package net.htlgkr.skywatcher.http;

public interface HttpListener<T> {
    void onSuccess(T response);
    void onError(String error);
}
