package com.example.photo_frame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class TokenActivity extends AppCompatActivity {


    public static final String CLIENT_ID = "942c740a6cd1451bacb4c2605b3c3ac3";

    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;

    public static final String USERNAME = "example.username";
    public static final String TOKEN = "example.token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri != null) {
            String mainPart = uri.toString().split("#")[1];
            String[] arguments = mainPart.split("&");
            String argument = arguments[0];
            String token = argument.split("=")[1];
            saveToken(token);
            finish();
        }

    }

    private void saveToken(String token) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(USERNAME, "");
        editor.putString(TOKEN, token);
        editor.apply();
    }

}
