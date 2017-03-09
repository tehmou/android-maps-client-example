package com.tehmou.book.androidmapsclientexample.network;

import android.graphics.Bitmap;

import io.reactivex.Observable;

public interface MapNetworkAdapter {
    Observable<Bitmap> getMapTile(int zoom, int x, int y);
    int getTileSizePx();
}
