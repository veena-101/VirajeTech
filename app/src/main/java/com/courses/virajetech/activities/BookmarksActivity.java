package com.courses.virajetech.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.BookMarksAdapter;
import com.courses.virajetech.model.BookMarks;
import com.courses.virajetech.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgBack;
    private TextView tvTitle, tvNoBookmarks;
    private RecyclerView recyclerView;

    private BookMarksAdapter adapter;
    private List<BookMarks> bookMarksList;

    private String userID = "234"; // Replace with actual userID retrieval logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        recyclerView = findViewById(R.id.rv_bookmarks);
        tvNoBookmarks = findViewById(R.id.tv_no_bookmarks);

        tvTitle.setText(getString(R.string.my_bookmarks));

        imgBack.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userID = getUserID(); // Use your actual method here

        getMyBookmarks();
    }

    private void getMyBookmarks() {
        bookMarksList = new ArrayList<>();

        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.BOOKMARKS + userID, null,
                response -> {
                    Utils.dissmisProgress();

                    try {
                        int status = response.getInt("status");

                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<BookMarks>() {}.getType();
                                BookMarks bookmark = gson.fromJson(jsonArray.get(i).toString(), type);
                                bookMarksList.add(bookmark);
                            }

                            if (!bookMarksList.isEmpty()) {
                                tvNoBookmarks.setVisibility(View.GONE);
                                adapter = new BookMarksAdapter(BookmarksActivity.this, bookMarksList, userID);
                                recyclerView.setAdapter(adapter);
                            } else {
                                tvNoBookmarks.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(this, response.optString("message", "No bookmarks found"), Toast.LENGTH_SHORT).show();
                            tvNoBookmarks.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(this, "Network error! Please try again.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjReq);
    }

    // Replace this with actual method or inherit from BaseActivity if needed
    private String getUserID() {
        // Retrieve from SharedPreferences or global app state
        return "123"; // Placeholder
    }
}
