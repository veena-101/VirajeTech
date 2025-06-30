package com.courses.virajetech.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppController extends Application {

    private static AppController mInstance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean networkCheck = true;
    private boolean loadActivity = false;
    private boolean settingSelected = false;

    private String userID = "";
    private String userName = "";
    private String email = "";
    private String phone = "";
    private String FCM_Id = "";
    private String profilePic = "";


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Override default font if needed
        Utils.overrideFont(this, "DEFAULT", "fonts/Sumana-Regular.ttf");

        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Load data from storage
        loadData();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void loadData() {
        userID = sharedPreferences.getString("user_id", "");
        userName = sharedPreferences.getString("user_name", "");
        profilePic = sharedPreferences.getString("profilePic", "");
        FCM_Id = sharedPreferences.getString("fcm_token", "");
        networkCheck = sharedPreferences.getBoolean("networkCheck", true);
    }

    public void saveData() {
        editor.putString("user_id", userID != null ? userID : "");
        editor.putString("user_name", userName != null ? userName : "");
        editor.putString("profilePic", profilePic != null ? profilePic : "");
        editor.putString("fcm_token", FCM_Id != null ? FCM_Id : "");
        editor.putBoolean("networkCheck", networkCheck);
        editor.apply(); // Use apply() instead of commit() for async
    }

    public void logout() {
        userID = null;
        userName = null;
        profilePic = null;
        email = null;
        phone = null;
        FCM_Id = null;
        loadActivity = false;
        settingSelected = false;

        editor.clear();
        editor.apply();
    }

    // Getters and setters

    public boolean getNetworkCheck() {
        return networkCheck;
    }

    public void setNetworkCheck(boolean networkCheck) {
        this.networkCheck = networkCheck;
    }

    public boolean getLoadActivity() {
        return loadActivity;
    }

    public void setLoadActivity(boolean loadActivity) {
        this.loadActivity = loadActivity;
    }

    public boolean getSettingSelected() {
        return settingSelected;
    }

    public void setSettingSelected(boolean settingSelected) {
        this.settingSelected = settingSelected;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFCM_Id() {
        return FCM_Id;
    }

    public void setFCM_Id(String FCM_Id) {
        this.FCM_Id = FCM_Id;
        editor.putString("fcm_token", FCM_Id);
        editor.apply();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
