package com.tehmou.book.androidmapsclientexample.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.tehmou.book.androidmapsclientexample.DrawableTile;
import com.tehmou.book.androidmapsclientexample.Tile;
import com.tehmou.book.androidmapsclientexample.TileBitmap;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class TileBitmapLoader {
    private static final String TAG = TileBitmapLoader.class.getSimpleName();
    private final MapNetworkAdapter mapNetworkAdapter;
    private final Map<Integer, Bitmap> loadedTileBitmaps = new ConcurrentHashMap<>();
    private final Subject<Object> tilesUpdateSubject = PublishSubject.create();

    public TileBitmapLoader(final MapNetworkAdapter mapNetworkAdapter) {
        this.mapNetworkAdapter = mapNetworkAdapter;
    }

    public void load(Collection<DrawableTile> mapTileDrawables) {
        Observable.fromIterable(mapTileDrawables)
                .filter(mapTileDrawable ->
                        !loadedTileBitmaps.containsKey(mapTileDrawable.tileHashCode()))
                .flatMap(this::loadTileBitmap)
                .map(mapTileBitmap -> {
                    if (mapTileBitmap != null && mapTileBitmap.getBitmap() != null) {
                        loadedTileBitmaps.put(mapTileBitmap.getMapTile().tileHashCode(),
                                mapTileBitmap.getBitmap());
                    }
                    return loadedTileBitmaps;
                })
                .subscribe(tile -> tilesUpdateSubject.onNext(new Object()));
    }

    public Bitmap getBitmap(Tile tile) {
        final int hash = tile.tileHashCode();
        if (loadedTileBitmaps.containsKey(hash)) {
            return loadedTileBitmaps.get(hash);
        }
        return null;
    }

    public Observable<Object> bitmapsLoadedEvent() {
        return tilesUpdateSubject.hide();
    }

    private Observable<TileBitmap> loadTileBitmap(final Tile mapTile) {
        Log.d(TAG, "Loading bitmap for tile " + mapTile.toString());
        try {
            return MapTileNetworkUtils.loadMapTile(mapNetworkAdapter).apply(mapTile);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }
}
