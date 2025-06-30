package com.courses.virajetech.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.ExamSeriesAdapter;
import com.courses.virajetech.model.ExamsSeries;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExamsSeriesActivity extends BaseActivity {

    private RecyclerView rvExamSeries;
    private TextView tvTitle, noSeries;
    private ImageView imgBack;
    private Toolbar toolbar;
    private Button updateSettings;
    private SearchView searchView;

    private List<ExamsSeries> examsSeriesList;
    private ExamSeriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_series);

        // Initialize Views
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        rvExamSeries = findViewById(R.id.rv_exams_series);
        noSeries = findViewById(R.id.tv_no_exams_series);
        updateSettings = findViewById(R.id.btn_exam_series_update_settings);

        tvTitle.setText(getString(R.string.exam_series));

        imgBack.setOnClickListener(v -> onBackPressed());

        setSupportActionBar(toolbar);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvExamSeries.setLayoutManager(layoutManager);

        updateSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ExamsSeriesActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (appState.getNetworkCheck()) {
            getExamSeriesData();
        } else {
            Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getExamSeriesData() {
        examsSeriesList = new ArrayList<>();

        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Utils.EXAMS_SERIES + getUserID();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        Log.e("exam_series", response.toString());

                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("records");
                            updateSettings.setVisibility(View.GONE);
                            noSeries.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ExamsSeries>() {}.getType();
                                ExamsSeries series = gson.fromJson(jsonArray.get(i).toString(), type);
                                examsSeriesList.add(series);
                            }

                            if (!examsSeriesList.isEmpty()) {
                                adapter = new ExamSeriesAdapter(ExamsSeriesActivity.this, examsSeriesList);
                                rvExamSeries.setAdapter(adapter);
                            } else {
                                noSeries.setVisibility(View.VISIBLE);
                            }

                        } else {
                            String msg = response.getString("message");
                            noSeries.setVisibility(View.VISIBLE);
                            noSeries.setText(msg);
                            updateSettings.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.dissmisProgress();
                    }
                }, errorListener);
        queue.add(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (examsSeriesList != null && !examsSeriesList.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (examsSeriesList != null && !examsSeriesList.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }
        });

        return true;
    }
}
