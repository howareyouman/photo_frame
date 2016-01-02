package com.example.photo_frame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    public static final String FRAGMENT_TAG = "list";

    public static final String CLIENT_ID = "942c740a6cd1451bacb4c2605b3c3ac3";

    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;

    public static final String USERNAME = "example.username";
    public static final String TOKEN = "example.token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getData() != null) {
            onLogin();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(TOKEN, null);
        if (token == null) {
            startLogin();
            //return;
        }

        if (savedInstanceState == null) {
            //startFragment();
            TextView text  = (TextView)findViewById(R.id.text);
            text.setText(token);

        }
    }
    /*
    public void reloadContent() {
        ListExampleFragment fragment = (ListExampleFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        fragment.restartLoader();
    }

    private void startFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ListExampleFragment(), FRAGMENT_TAG)
                .commit();
    }
    */
    public void startLogin() {
        new AuthDialogFragment().show(getSupportFragmentManager(), "auth");
    }

    private void onLogin () {
        Uri data = getIntent().getData();
        setIntent(null);
        Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
        Matcher matcher = pattern.matcher(data.toString());
        if (matcher.find()) {
            final String token = matcher.group(1);
            if (!TextUtils.isEmpty(token)) {
                Log.d(TAG, "onLogin: token: " + token);
                saveToken(token);
            } else {
                Log.w(TAG, "onRegistrationSuccess: empty token");
            }
        } else {
            Log.w(TAG, "onRegistrationSuccess: token not found in return url");
        }
    }

    private void saveToken(String token) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(USERNAME, "");
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static class AuthDialogFragment extends DialogFragment {

        public AuthDialogFragment () {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.example_auth_title)
                    .setMessage(R.string.example_auth_message)
                    .setPositiveButton(R.string.example_auth_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)));
                        }
                    })
                    .setNegativeButton(R.string.example_auth_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .create();
        }
    }
}
