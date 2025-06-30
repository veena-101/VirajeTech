package com.courses.virajetech.notifications;

import android.content.SharedPreferences;
import android.util.Log;

import com.courses.virajetech.utils.AppController;
import com.courses.virajetech.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    AppController appState;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);

        appState = (AppController) getApplicationContext();

        // Save token in shared preferences
        storeRegIdInPref(token);
        appState.setFCM_Id(token);

        // Optionally send token to server
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}
