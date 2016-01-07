package com.example.photo_frame;

/**
 * Created by Арсений on 1/7/2016.
 */
public class Token {
    static private String token;
    private Token(){};
    public static void setToken(String t) {
        if(token == null && t!= null)
            token = t;
    }
    public static String getToken() { return token; }
}
