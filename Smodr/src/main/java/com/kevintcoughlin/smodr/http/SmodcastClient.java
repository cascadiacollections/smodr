package com.kevintcoughlin.smodr.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kevintcoughlin.smodr.models.Rss;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Client for making network requests to fetch feed data.
 *
 * @author kevincoughlin
 */
public final class SmodcastClient {
	/**
	 * The network request client.
	 */
	@Nullable
	private static SmodcastInterface sSmodcastService;

	/**
	 * Returns a client to make network requests.
	 *
	 * @return
	 *      the {@link com.kevintcoughlin.smodr.http.SmodcastClient.SmodcastInterface} client.
	 */
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

	/**
	 * Interface for fetching Smodcast feed data.
	 */
	public interface SmodcastInterface {
        @GET("/{channel}/feed/")
        Observable<Rss> getFeed(@NonNull final @Path("channel") String channel);
    }
}
