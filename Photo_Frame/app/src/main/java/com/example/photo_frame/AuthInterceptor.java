package com.example.photo_frame;

import retrofit.RequestInterceptor;

/**
 * Created by Арсений on 1/7/2016.
 */
class AuthInterceptor implements RequestInterceptor {
    @Override
    public void intercept(RequestFacade req) {
        String token = Token.getToken();
        if (token != null) {
            req.addHeader("Authorization", "OAth "+token);
        }
    }
}