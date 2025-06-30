package com.courses.virajetech.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import com.android.installreferrer.BuildConfig;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.PopularExamsAdapter;
import com.courses.virajetech.adapters.PopularLmsAdapter;
import com.courses.virajetech.model.CategoryListLMS;
import com.courses.virajetech.model.PopularExams;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private NavigationView navigationView;

    ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    NestedScrollView scrollView;
    private Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    RecyclerView popularExams, popularLMS;

    PopularExamsAdapter examsAdapter;
    PopularLmsAdapter lmsAdapter;

    CardView imgExams, imgLms, imgExamsSeries, imgLmsSeries, imgNotifications, imgAnalysis;

    TextView tvTitle, appVersion, noPopularExams, noPopularLMS;
    CircleImageView userPic;
    String  profilePic;

    List<PopularExams> popularExamsList;
    List<CategoryListLMS> popularLMSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColor(this, R.color.light_violet_color);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        scrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        popularExams = (RecyclerView) findViewById(R.id.rv_popular_exams);
        popularLMS = (RecyclerView) findViewById(R.id.rv_popular_lms);

        imgExams = (CardView) findViewById(R.id.img_main_exams);
        imgLms = (CardView) findViewById(R.id.img_main_lms);
        imgAnalysis = (CardView) findViewById(R.id.img_main_analysis);
        imgNotifications = (CardView) findViewById(R.id.img_main_notifications);
        imgExamsSeries = (CardView) findViewById(R.id.img_main_exams_series);
        imgLmsSeries = (CardView) findViewById(R.id.img_main_lms_series);

        noPopularExams = findViewById(R.id.tv_no_popular_exams);

        noPopularLMS = findViewById(R.id.tv_no_popular_lms);

        appVersion = (TextView) findViewById(R.id.tv_app_version);
        BuildConfig BuildConfig = null;
        appVersion.setText(getString(R.string.vernsion)+" "+ BuildConfig.VERSION_NAME);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.white_color));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularExams.setLayoutManager(mLayoutManager);
        popularExams.setItemAnimator(new DefaultItemAnimator());


        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularLMS.setLayoutManager(pLayoutManager);
        popularLMS.setItemAnimator(new DefaultItemAnimator());


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawer.closeDrawers();
                displayContentView(menuItem.getItemId());
                return true;
            }
        });

        imgExams.setOnClickListener(this);
        imgLms.setOnClickListener(this);
        imgAnalysis.setOnClickListener(this);
        imgNotifications.setOnClickListener(this);
        imgExamsSeries.setOnClickListener(this);
        imgLmsSeries.setOnClickListener(this);


        if (appState.getNetworkCheck()) {
            getPopularRecords();
        } else {
            Toast.makeText(this, getResources().getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }

        GradientDrawable popularExams = (GradientDrawable) noPopularExams.getBackground();
        popularExams.setStroke(2, ContextCompat.getColor(this, R.color.orange_300));


        GradientDrawable popularLMS = (GradientDrawable) noPopularLMS.getBackground();
        popularLMS.setStroke(2, ContextCompat.getColor(this, R.color.blue_color));



    }

    @Override
    protected void onStart() {
        super.onStart();

        initDrawer();

        collapsingToolbarLayout.setTitle(appState.getUserName());

    }


    public void initDrawer() {

        View headerview = navigationView.getHeaderView(0);

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intent);
                drawer.closeDrawers();
            }
        });


        tvTitle = (TextView) headerview.findViewById(R.id.header_title_name);
        tvTitle.setText(appState.getUserName());
        setTextColorGradient(tvTitle, getResources().getColor(R.color.update_my_profile_primary), getResources().getColor(R.color.update_my_profile));

        userPic = (CircleImageView) headerview.findViewById(R.id.img_header_user_pic);

        if (sharedPreferences.contains("profilePic")) {

            profilePic = sharedPreferences.getString("profilePic", profilePic);

            String imagePath = Utils.USER_PIC_BASE_URL + profilePic;


            if (profilePic != null && !profilePic.equals("null") && !profilePic.equals("")) {
                Glide.with(this)
                        .load(imagePath)
                        .into(userPic);
            } else {
                Glide.with(this)
                        .load(Utils.USER_PIC_BASE_URL + "default.png")
                        .into(userPic);
            }

        } else {

            Glide.with(this)
                    .load(Utils.USER_PIC_BASE_URL + "default.png")
                    .into(userPic);
        }

    }

    private void displayContentView(int id) {
        Intent intent;

        if (id == R.id.nav_home) {
            if (appState.getNetworkCheck()) {
                getPopularRecords();
            } else {
                Toast.makeText(this, getResources().getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_examhistory) {
            intent = new Intent(this, ExamsHistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_feedback) {
            intent = new Intent(this, AddFeedBackActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_bookmarks) {
            intent = new Intent(this, BookmarksActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share_app) {
            shareApp();

        } else if (id == R.id.nav_subscription) {
            intent = new Intent(this, SubscriptionActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Menorah OES");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose one"));
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;

        if (id == R.id.img_main_exams) {
            intent = new Intent(this, ExamsActivity.class);
            startActivity(intent);

        } else if (id == R.id.img_main_lms) {
            intent = new Intent(this, LMSActivity.class);
            startActivity(intent);

        } else if (id == R.id.img_main_analysis) {
            intent = new Intent(this, AnalysisActivity.class);
            startActivity(intent);

        } else if (id == R.id.img_main_notifications) {
            intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);

        } else if (id == R.id.img_main_exams_series) {
            intent = new Intent(this, ExamsSeriesActivity.class);
            startActivity(intent);

        } else if (id == R.id.img_main_lms_series) {
            intent = new Intent(this, LMSSeriesActivity.class);
            startActivity(intent);
        }
    }


    private long exitTime = 0;


    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void getPopularRecords() {

        popularExamsList = new ArrayList<>();
        popularLMSList = new ArrayList<>();
        Utils.showProgressDialog(this, "Loggin/Registering...");
        Utils.showProgress(this);
        Log.e("popular_records", Utils.POPULAR_RECORDS + "?user_id=" + getUserID());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.POPULAR_RECORDS + "?user_id=" + getUserID(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("popular_esp", response + "");
                        Utils.dissmisProgress();

                        final JSONArray jsonArrayExams, jsonArrayLMS;
                        int status;

                        try {
                            status = response.getInt("status");

                            if (status == 1) {
                                jsonArrayExams = response.getJSONArray("exam_records");
                                jsonArrayLMS = response.getJSONArray("lms_records");

                                noPopularExams.setVisibility(View.GONE);
                                noPopularLMS.setVisibility(View.GONE);

                                for (int i = 0; i < jsonArrayExams.length(); i++) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<PopularExams>() {}.getType();
                                    PopularExams myQuestions = gson.fromJson(jsonArrayExams.get(i).toString(), type);
                                    popularExamsList.add(myQuestions);

                                }

                                for (int i = 0; i < jsonArrayLMS.length(); i++) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<CategoryListLMS>() {}.getType();
                                    CategoryListLMS myQuestions = gson.fromJson(jsonArrayLMS.get(i).toString(), type);
                                    popularLMSList.add(myQuestions);

                                }

                                if (popularLMSList.size() != 0) {
                                    popularLMS.setVisibility(View.VISIBLE);
                                    lmsAdapter = new PopularLmsAdapter(MainActivity.this, popularLMSList);
                                    popularLMS.setAdapter(lmsAdapter);
                                } else {
                                    popularLMS.setVisibility(View.GONE);
                                    noPopularLMS.setVisibility(View.VISIBLE);
                                }

                                if (popularExamsList.size() != 0) {
                                    popularExams.setVisibility(View.VISIBLE);
                                    examsAdapter = new PopularExamsAdapter(MainActivity.this, popularExamsList);
                                    popularExams.setAdapter(examsAdapter);
                                } else {
                                    noPopularExams.setVisibility(View.VISIBLE);
                                    popularExams.setVisibility(View.GONE);

                                }

                            } else {
                                popularExams.setVisibility(View.GONE);
                                popularLMS.setVisibility(View.GONE);
                                noPopularExams.setVisibility(View.VISIBLE);
                                noPopularLMS.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, errorListener) {


        };
        queue.add(jsonObjReq);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.dissmisProgress();
    }
}
