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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.CategoryExamsListAdapter;
import com.courses.virajetech.model.Categories;
import com.courses.virajetech.model.ExamCategoryList;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryExamsListActivity extends AppCompatActivity {

    private TextView tvTitle, noExamsList;
    private ImageView imgBack;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private CategoryExamsListAdapter adapter;
    private List<ExamCategoryList> categoryExamsLists;

    private Categories category;
    private String userID = "123"; // TODO: Replace with real logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_exams_list);

        // Init Views
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        noExamsList = findViewById(R.id.tv_no_category_exams_list);
        recyclerView = findViewById(R.id.rv_category_exams_list);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        imgBack.setOnClickListener(v -> onBackPressed());

        if (getIntent().getSerializableExtra("category") != null) {
            category = (Categories) getIntent().getSerializableExtra("category");
            tvTitle.setText(category.getCategory());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.showProgress(this)) {
            getCategoryList();
        } else {
            Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoryList() {
        categoryExamsLists = new ArrayList<>();
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Utils.GET_CATEGORY_EXAM_LIST + category.getSlug() + "?user_id=" + userID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("exams");
                            noExamsList.setVisibility(View.GONE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ExamCategoryList>() {}.getType();
                                ExamCategoryList exam = gson.fromJson(jsonArray.get(i).toString(), type);
                                categoryExamsLists.add(exam);
                            }
                            if (!categoryExamsLists.isEmpty()) {
                                adapter = new CategoryExamsListAdapter(this, categoryExamsLists);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                noExamsList.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            String msg = response.getString("message");
                            noExamsList.setVisibility(View.VISIBLE);
                            noExamsList.setText(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(this, getString(R.string.vernsion), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });

        queue.add(request);
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
                if (categoryExamsLists != null && !categoryExamsLists.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (categoryExamsLists != null && !categoryExamsLists.isEmpty()) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }
        });

        return true;
    }
}
