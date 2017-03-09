package com.tehmou.book.androidmapsclientexample.network;

import android.util.Log;

import com.tehmou.book.androidmapsclientexample.Tile;
import com.tehmou.book.androidmapsclientexample.TileBitmap;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class MapTileNetworkUtils {
    private static final String TAG = MapTileNetworkUtils.class.getSimpleName();

    static public Function<Tile, Observable<TileBitmap>> loadMapTile(
            final MapNetworkAdapter mapNetworkAdapter) {
        return mapTile -> mapNetworkAdapter.getMapTile(
                mapTile.getZoom(), mapTile.getX(), mapTile.getY())
                .map(bitmap -> new TileBitmap(mapTile, bitmap))
                .onErrorResumeNext(throwable -> {
                    Log.e(TAG, "Error loading tile (" + mapTile + ")", throwable);
                    throwable.printStackTrace();
                    return Observable.just(new TileBitmap(mapTile, null));
                });
    }
}
