package com.kevintcoughlin.smodr.http;

import android.support.annotation.Nullable;
import com.kevintcoughlin.smodr.models.Rss;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;

public final class SmodcastClient {
	@Nullable
	private static SmodcastInterface sSmodcastService;

    public static SmodcastInterface getClient() {
        if (sSmodcastService == null) {
            final RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://smodcast.com/channels")
                    .setConverter(new SimpleXMLConverter())
                    .build();
            sSmodcastService = restAdapter.create(SmodcastInterface.class);
        }

        return sSmodcastService;
    }

    public interface SmodcastInterface {
        @GET("/{channel}/feed/")
        void getFeed(final @Path("channel") String channel, final Callback<Rss> callback);
    }
}
