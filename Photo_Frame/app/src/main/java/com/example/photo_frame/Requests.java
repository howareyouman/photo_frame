package com.example.photo_frame;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Арсений on 1/7/2016.
 */
public interface Requests {
    @GET("/v1/disk/resources/download?path={path}")
    void getUrlOfFile(@Path("path") String path, Callback<Response> callback);

}
