package com.courses.virajetech.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.CategoriesAdapter;
import com.courses.virajetech.model.Categories;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LMSActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvTitle, noCategories;
    private ImageView imgBack;
    private RecyclerView recyclerView;
    private Button updateSettings;
    private SearchView searchView;

    private List<Categories> categoriesList = new ArrayList<>();
    private CategoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lms);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        updateSettings = findViewById(R.id.btn_lms_update_settings);
        noCategories = findViewById(R.id.tv_no_lms_categories_list);
        recyclerView = findViewById(R.id.rv_lms_categories);

        // Toolbar setup
        setSupportActionBar(toolbar);
        tvTitle.setText(R.string.lms_cat);
        imgBack.setOnClickListener(v -> onBackPressed());

        // Layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Update settings button
        updateSettings.setOnClickListener(v -> {
            startActivity(new Intent(LMSActivity.this, SettingsActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (appState.getNetworkCheck()) {
            getCategoriesList();
        } else {
            Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoriesList() {
        categoriesList.clear();
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        String url = Utils.LMS_CATEGORIES_LIST + getUserID();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("categories");
                            updateSettings.setVisibility(View.GONE);
                            noCategories.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<Categories>() {}.getType();
                                Categories category = gson.fromJson(jsonArray.get(i).toString(), type);
                                categoriesList.add(category);
                            }

                            if (!categoriesList.isEmpty()) {
                                adapter = new CategoriesAdapter((Activity) LMSActivity.this, categoriesList, "lms");
                                recyclerView.setAdapter(adapter);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                showNoDataMessage(getString(R.string.no_exams_found));
                            }

                        } else {
                            showNoDataMessage(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showNoDataMessage("Error parsing data");
                    }
                }, errorListener);
        queue.add(jsonObjReq);
    }

    private void showNoDataMessage(String message) {
        recyclerView.setVisibility(View.GONE);
        noCategories.setVisibility(View.VISIBLE);
        updateSettings.setVisibility(View.VISIBLE);
        noCategories.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) item.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!categoriesList.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!categoriesList.isEmpty()) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }
}
