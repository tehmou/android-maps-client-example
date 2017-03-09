package com.tehmou.book.androidmapsclientexample.network;

import android.graphics.Bitmap;

import io.reactivex.Observable;

public interface NetworkClient {
    Observable<String> loadString(final String url);
    Observable<Bitmap> loadBitmap(final String url);
}