package com.courses.virajetech.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.ExamsHistoryAdapter;
import com.courses.virajetech.model.ExamHistory;
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

public class ExamsHistoryActivity extends BaseActivity {

    private TextView tvTitle, noExams;
    private ImageView imgBack;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private List<ExamHistory> examsHistoryList = new ArrayList<>();
    private ExamsHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_history);

        initViews();
        setupToolbar();
        setupRecyclerView();

        // Fetch exams history
        if (appState.getNetworkCheck()) {
            getExamsList();
        } else {
            Utils.showProgress(this);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        recyclerView = findViewById(R.id.rv_exams_history);
        noExams = findViewById(R.id.tv_no_exam_history);
    }

    private void setupToolbar() {
        tvTitle.setText(getString(R.string.exams_history));
        imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getExamsList() {
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        String url = Utils.GET_EXAMS_HISTORY + getUserID();
        Log.d("ExamsHistory", "Fetching exams from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Utils.dissmisProgress();
                    parseExamsResponse(response);
                },
                errorListener);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseExamsResponse(JSONObject response) {
        try {
            JSONArray recordsArray = response.optJSONArray("records");

            if (recordsArray != null && recordsArray.length() > 0) {
                for (int i = 0; i < recordsArray.length(); i++) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ExamHistory>() {}.getType();
                    ExamHistory exam = gson.fromJson(recordsArray.get(i).toString(), type);
                    examsHistoryList.add(exam);
                }

                adapter = new ExamsHistoryAdapter(this, examsHistoryList);
                recyclerView.setAdapter(adapter);

                noExams.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                showNoExamsMessage();
            }

        } catch (JSONException e) {
            Log.e("ExamsHistory", "JSON Parsing error: ", e);
            showNoExamsMessage();
        }
    }

    private void showNoExamsMessage() {
        noExams.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
