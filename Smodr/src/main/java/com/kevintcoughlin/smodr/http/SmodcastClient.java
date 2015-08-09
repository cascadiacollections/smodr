package com.kevintcoughlin.smodr.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kevintcoughlin.smodr.models.Rss;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

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
        Observable<Rss> getFeed(@NonNull final @Path("channel") String channel);
    }
}
