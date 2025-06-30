package com.courses.virajetech.utils;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.*;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.*;
import com.courses.virajetech.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected AppController appState;
    protected Response.ErrorListener errorListener;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appState = (AppController) getApplicationContext();
        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initErrorListener();
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initErrorListener() {
        errorListener = error -> {
            String message;
            if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError) {
                message = "Cannot connect to Internet. Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "Server error. Try again later!";
            } else if (error instanceof ParseError) {
                message = "Parsing error. Try again!";
            } else if (error instanceof TimeoutError) {
                message = "Connection timed out. Please check your network.";
            } else {
                message = "Unknown error occurred.";
            }

            Utils.dissmisProgress();
            Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
        };
    }

    public void setDrawableColor(Button button, @ColorRes int colorRes) {
        for (Drawable drawable : button.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(
                        ContextCompat.getColor(this, colorRes),
                        PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void setStatusBarColor(Activity activity, @ColorRes int colorRes) {
        Window window = activity.getWindow();
        View decor = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decor.setSystemUiVisibility(flags);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, colorRes));
        }
    }

    public void setToolBarBackgroundColor(Toolbar toolbar, Activity activity, @ColorRes int colorRes) {
        toolbar.setBackgroundColor(ContextCompat.getColor(activity, colorRes));
    }

    public boolean getNetworkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public void setTextColorGradient(TextView textView, int colorStart, int colorEnd) {
        Shader shader = new LinearGradient(0, 0, 100, 0, colorStart, colorEnd, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
    }

    public long getImageSize(Uri uri) {
        String strPath = null;
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                strPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
            cursor.close();
        }

        if (strPath != null) {
            File file = new File(strPath);
            return file.length() / (1024 * 1024); // MB
        }

        return 0;
    }

    public String getUserID() {
        return sharedPreferences.getString("user_id", null);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String dateFormat(String inputDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = input.parse(inputDate);
            return output.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Date parsing failed: " + inputDate, e);
            return inputDate;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        appState.saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
    }

    private final BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = isOnline(context);
            appState.setNetworkCheck(connected);
        }
    };

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network == null) return false;
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            }

        } catch (Exception e) {
            Log.e(TAG, "Network check failed", e);
            return false;
        }
    }
}
