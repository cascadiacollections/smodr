package com.kevintcoughlin.smodr.services;

import com.kevintcoughlin.smodr.models.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FeedService {
    @GET
    Call<Feed> feed(@Url String url);
}
