package com.kevintcoughlin.smodr.http;

import com.kevintcoughlin.smodr.models.Rss;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;

public final class SmodcastClient {
    private static SmodcastInterface sSmodcastService;

    public static SmodcastInterface getClient() {
        if (sSmodcastService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://smodcast.com/channels")
                    .setConverter(new SimpleXMLConverter())
                    .build();

            sSmodcastService = restAdapter.create(SmodcastInterface.class);
        }

        return sSmodcastService;
    }

    public interface SmodcastInterface {
        @GET("/{channel}/feed/")
        void getFeed(@Path("channel") String channel, Callback<Rss> callback);
    }
}
