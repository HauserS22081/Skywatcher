package net.htlgkr.skywatcher;

public interface HttpListener<T> {
    void onSuccess(T response);
    void onError(String error);
}
