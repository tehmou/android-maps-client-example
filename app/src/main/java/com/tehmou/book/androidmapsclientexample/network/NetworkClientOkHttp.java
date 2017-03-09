package com.tehmou.book.androidmapsclientexample.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class NetworkClientOkHttp implements NetworkClient {
    private static final String TAG = NetworkClientOkHttp.class.getCanonicalName();
    final private OkHttpClient client;

    public NetworkClientOkHttp() {
        client = new OkHttpClient();
    }

    public Observable<String> loadString(final String url) {
        Log.i(TAG, "loadString(" + url + ")");
        return Observable
                .create(new LoadString(url))
                .subscribeOn(Schedulers.io());
    }

    public Observable<Bitmap> loadBitmap(final String url) {
        Log.i(TAG, "loadBitmap(" + url + ")");
        return Observable
                .create(new LoadBitmap(url))
                .subscribeOn(Schedulers.io());
    }

    abstract private class LoadOperation<T> implements ObservableOnSubscribe<T> {
        final protected String url;

        public LoadOperation(final String url) {
            this.url = url;
        }
    }

    private class LoadString extends LoadOperation<String> {
        public LoadString(final String url) {
            super(url);
        }

        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            try {
                final Request request = new Request.Builder()
                        .url(url)
                        .build();
                final Response response = client.newCall(request).execute();
                final String responseBody = response.body().string();
                emitter.onNext(responseBody);
                emitter.onComplete();
            } catch (final Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }
    }

    private class LoadBitmap extends LoadOperation<Bitmap> {
        public LoadBitmap(final String url) {
            super(url);
        }

        @Override
        public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
            try {
                final URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                emitter.onNext(bitmap);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }
    }
}
