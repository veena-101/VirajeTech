package com.courses.virajetech.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.android.volley.Response;
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
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExamsActivity extends BaseActivity {


    RecyclerView recyclerView;

    private ArrayList<Categories> categoriesList;
    CategoriesAdapter adapter;

    TextView tvTitle, noCategories;
    ImageView imgBack;
    Toolbar toolbar;

    Button updateSettings ;

    SearchView searchView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
       // setStatusBarColor(this, R.color.analysis_bg_primary);

        recyclerView = (RecyclerView) findViewById(R.id.rv_exams_categories);
        imgBack = (ImageView) findViewById(R.id.img_toolbar_back);
        tvTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        noCategories = (TextView) findViewById(R.id.tv_no_exams_categories_list);

        updateSettings = findViewById(R.id.btn_exams_update_settings);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tvTitle.setText(getString(R.string.exam_cat));
        //setToolBarBackgroundColor(toolbar, this, R.color.analysis_bg_primary);



        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ExamsActivity.this,SettingsActivity.class);
                startActivity(intent);

            }
        });

        // white background notification bar
        //whiteNotificationBar(recyclerView);

        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (appState.getNetworkCheck()) {
            getCategoriesList();
        } else {

            Toast.makeText(this, getResources().getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void getCategoriesList() {

        categoriesList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.EXAMS_CATEGORIES_LIST + getUserID(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dissmisProgress();
                        String mssg;
                        int status;
                        final JSONArray jsonArray;
                        try {

                            Log.e("exams",response+"");

                            status = response.getInt("status");
                            if (status == 1) {
                                jsonArray = response.getJSONArray("categories");
                                noCategories.setVisibility(View.GONE);
                                updateSettings.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<Categories>() {
                                    }.getType();
                                    Categories myQuestions = gson.fromJson(jsonArray.get(i).toString(), type);
                                    categoriesList.add(myQuestions);

                                }

                                if (categoriesList.size() != 0) {
                                    adapter = new CategoriesAdapter(ExamsActivity.this, categoriesList, "exams");
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    noCategories.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }

                            } else {
                                mssg = response.getString("message");
                                noCategories.setVisibility(View.VISIBLE);
                                noCategories.setText(mssg);
                                updateSettings.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if(categoriesList.size()!=0){
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed

                if(categoriesList.size()!=0){
                    adapter.getFilter().filter(query);
                }

                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
