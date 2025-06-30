package com.courses.virajetech.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.courses.virajetech.R;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME_OUT = 2500;
    private static final String TAG = "SplashActivity";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView imageView;
    private TextView tvAppTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        imageView = findViewById(R.id.img_splash);
        tvAppTitle = findViewById(R.id.tv_splash_title);

        // SharedPreferences setup
        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Subscribe to a Firebase topic
        FirebaseMessaging.getInstance().subscribeToTopic("topic")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to FCM topic successfully.");
                    } else {
                        Log.e(TAG, "FCM topic subscription failed", task.getException());
                    }
                });

        // Get FCM token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                Log.d(TAG, "FCM Token: " + fcmToken);
                appState.setFCM_Id(fcmToken);
            } else {
                Log.e(TAG, "Failed to fetch FCM token", task.getException());
            }
        });

        // Splash screen delay and navigation logic
        new Handler().postDelayed(() -> {
            String userId = sharedPreferences.getString("user_id", null);

            if (userId != null && !userId.trim().isEmpty()) {
                appState.setUserID(userId);
                Log.d(TAG, "User ID found. Navigating to MainActivity.");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                Log.d(TAG, "No user ID found. Navigating to LoginActivity.");
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        }, SPLASH_TIME_OUT);
    }
}
