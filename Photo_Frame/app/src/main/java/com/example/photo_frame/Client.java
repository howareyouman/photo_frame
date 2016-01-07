package com.example.photo_frame;

import retrofit.RestAdapter;

/**
 * Created by ThinkPad on 31.10.2015.
 */
public class Client{
    static private Requests client;
    private static final String servUrl = "https://cloud-api.yandex.net";
    private Client(){}
    public static Requests sharedInstance(){
        if(client == null){
            RestAdapter what = new RestAdapter.Builder().setEndpoint(servUrl).setRequestInterceptor(new AuthInterceptor()).build();
            client = what.create(Requests.class);
        }
        return client;
    }
}
