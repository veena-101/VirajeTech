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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.LMSCategoryListAdapter;
import com.courses.virajetech.model.Categories;
import com.courses.virajetech.model.CategoryListLMS;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LMSCategoryList extends BaseActivity {
    private Toolbar toolbar;
    private ImageView imgBack;
    private TextView tvTitle, noSeries;
    private RecyclerView recyclerView;
    private LMSCategoryListAdapter adapter;
    private List<CategoryListLMS> list = new ArrayList<>();
    private Categories category;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmscategory_list);

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        recyclerView = findViewById(R.id.rv_lms_category_list);
        noSeries = findViewById(R.id.tv_no_lms_category);

        setSupportActionBar(toolbar);

        // Set back button behavior
        imgBack.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get selected category from intent
        if (getIntent().getSerializableExtra("category") != null) {
            category = (Categories) getIntent().getSerializableExtra("category");
            tvTitle.setText(category.getCategory());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appState.getNetworkCheck()) {
            getCategoryLMSList();
        } else {
            Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoryLMSList() {
        list.clear();
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        String url = Utils.GET_CATEGORY_LMS_LIST + category.getSlug() + "?user_id=" + getUserID();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        JSONArray jsonArray = response.getJSONArray("records");
                        noSeries.setVisibility(View.GONE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CategoryListLMS>() {}.getType();
                            CategoryListLMS item = gson.fromJson(jsonArray.get(i).toString(), type);
                            list.add(item);
                        }

                        if (!list.isEmpty()) {
                            adapter = new LMSCategoryListAdapter(LMSCategoryList.this, list);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showNoDataMessage(getString(R.string.no_exams_found));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showNoDataMessage("Error loading data");
                    }
                },
                errorListener
        );
        queue.add(request);
    }

    private void showNoDataMessage(String message) {
        recyclerView.setVisibility(View.GONE);
        noSeries.setVisibility(View.VISIBLE);
        noSeries.setText(message);
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
                if (!list.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!list.isEmpty()) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }
}
