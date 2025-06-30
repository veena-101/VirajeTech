package com.courses.virajetech.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.ExamSeriesListAdapter;
import com.courses.virajetech.model.ExamSeriesList;
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

public class ExamsSeriesList extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle, noSeries;
    private ImageView imgBack;
    private Toolbar toolbar;
    private SearchView searchView;

    private ExamsSeries examsSeries;
    private List<ExamSeriesList> examsSeriesLists;
    private ExamSeriesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_series_list);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        recyclerView = findViewById(R.id.rv_exams_series_list);
        noSeries = findViewById(R.id.tv_no_exams_series_list);

        setSupportActionBar(toolbar);

        imgBack.setOnClickListener(v -> onBackPressed());

        if (getIntent().getSerializableExtra("exam_series_list") != null) {
            examsSeries = (ExamsSeries) getIntent().getSerializableExtra("exam_series_list");
            tvTitle.setText(examsSeries.getTitle());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (appState.getNetworkCheck()) {
            getExamSeriesListData();
        } else {
            Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getExamSeriesListData() {
        examsSeriesLists = new ArrayList<>();

        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        String url = Utils.GET_EXAM_SERIES_LIST + examsSeries.getSlug() + "?user_id=" + getUserID();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("items_list");
                            noSeries.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ExamSeriesList>() {}.getType();
                                ExamSeriesList item = gson.fromJson(jsonArray.get(i).toString(), type);
                                examsSeriesLists.add(item);
                            }

                            if (!examsSeriesLists.isEmpty()) {
                                adapter = new ExamSeriesListAdapter(ExamsSeriesList.this, examsSeriesLists);
                                recyclerView.setAdapter(adapter);
                            } else {
                                noSeries.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            String msg = response.getString("message");
                            noSeries.setVisibility(View.VISIBLE);
                            noSeries.setText(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                if (!examsSeriesLists.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!examsSeriesLists.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }
        });

        return true;
    }
}
