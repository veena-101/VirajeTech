package com.courses.virajetech.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.AnalysisByExamAdapter;
import com.courses.virajetech.adapters.SpinnerAnalysisSubjectsAdapter;
import com.courses.virajetech.model.AnalysisByExam;
import com.courses.virajetech.model.AnalysisBySubject;
import com.courses.virajetech.utils.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnalysisActivity extends AppCompatActivity {

    PieChart pieChart;
    Toolbar toolbar;
    ImageView imgBack;
    TextView tvTitle, tvViewChart, tvNoAnalysis;
    Spinner sprSubjects;
    LinearLayout llSubjects, llTitles;
    RecyclerView recyclerView;

    List<AnalysisByExam> analysisByExamList;
    List<AnalysisBySubject> analysisBySubjectList;
    ArrayList<PieEntry> yValues;
    AnalysisByExamAdapter adapter;

    int count = 1;
    String userID = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        pieChart = findViewById(R.id.piechart_1);
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        sprSubjects = findViewById(R.id.spr_analysis_subjects);
        llSubjects = findViewById(R.id.ll_subjects);
        recyclerView = findViewById(R.id.rv_analysis_by_exam);
        llTitles = findViewById(R.id.ll_analysis_titles);
        tvViewChart = findViewById(R.id.tv_analysis_view_chart);
        tvNoAnalysis = findViewById(R.id.tv_no_analysis);

        setSupportActionBar(toolbar);
        tvTitle.setText(getString(R.string.analysis_by_exam));

        imgBack.setOnClickListener(v -> onBackPressed());

        sprSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSubjectPieChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvViewChart.setOnClickListener(v -> {
            count++;
            if (count % 2 == 0) {
                tvViewChart.setText(getString(R.string.hide_chart));
                pieChart.setVisibility(View.VISIBLE);
            } else {
                tvViewChart.setText(getString(R.string.view_chart));
                pieChart.setVisibility(View.GONE);
            }
        });

        getAnalysisByExam();
    }

    private void getAnalysisBySubject() {
        analysisBySubjectList = new ArrayList<>();
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.ANALYSIS_BY_SUBJECT + userID, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("subjects");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<AnalysisBySubject>() {}.getType();
                                AnalysisBySubject subject = gson.fromJson(jsonArray.get(i).toString(), type);
                                analysisBySubjectList.add(subject);
                            }

                            pieChart.setVisibility(View.VISIBLE);
                            tvNoAnalysis.setVisibility(View.GONE);

                            SpinnerAnalysisSubjectsAdapter adapter = new SpinnerAnalysisSubjectsAdapter(this,
                                    android.R.layout.simple_spinner_dropdown_item, analysisBySubjectList);
                            sprSubjects.setAdapter(adapter);

                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(this, getString(R.string.vernsion), Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjReq);
    }

    private void getAnalysisByExam() {
        analysisByExamList = new ArrayList<>();
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.ANALYSIS_BY_EXAM + userID, null,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONArray jsonArray = response.getJSONArray("records");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<AnalysisByExam>() {}.getType();
                                AnalysisByExam exam = gson.fromJson(jsonArray.get(i).toString(), type);
                                analysisByExamList.add(exam);
                            }

                            if (!analysisByExamList.isEmpty()) {
                                setExamPieChart();
                                adapter = new AnalysisByExamAdapter(this, analysisByExamList);
                                recyclerView.setAdapter(adapter);
                                tvNoAnalysis.setVisibility(View.GONE);
                                llTitles.setVisibility(View.VISIBLE);
                            } else {
                                tvNoAnalysis.setVisibility(View.VISIBLE);
                                llTitles.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(this, getString(R.string.vernsion), Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjReq);
    }

    private void setExamPieChart() {
        pieChart.clear();
        yValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (AnalysisByExam item : analysisByExamList) {
            yValues.add(new PieEntry(Float.parseFloat(item.getAttempts()), item.getTitle()));
            Random rnd = new Random();
            colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        }

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new MyValueFormatter());

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawSliceText(false);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void setSubjectPieChart(int position) {
        if (analysisBySubjectList == null || position >= analysisBySubjectList.size()) return;

        yValues = new ArrayList<>();
        AnalysisBySubject subject = analysisBySubjectList.get(position);

        if (!subject.getCorrect_answers().equals("0")) {
            yValues.add(new PieEntry(Float.parseFloat(subject.getCorrect_answers()), getString(R.string.correct)));
        }
        if (!subject.getWrong_answers().equals("0")) {
            yValues.add(new PieEntry(Float.parseFloat(subject.getWrong_answers()), getString(R.string.wrong)));
        }
        if (!subject.getNot_answered().equals("0")) {
            yValues.add(new PieEntry(Float.parseFloat(subject.getNot_answered()), getString(R.string.not_answered)));
        }

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new MyValueFormatter());

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawSliceText(false);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_analysis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.by_exam) {
            tvTitle.setText(getString(R.string.analysis_by_exam));
            llSubjects.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            llTitles.setVisibility(View.VISIBLE);
            tvViewChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(count % 2 == 0 ? View.VISIBLE : View.GONE);
            getAnalysisByExam();
            return true;
        } else if (id == R.id.by_subject) {
            tvTitle.setText(getString(R.string.analysis_by_subject));
            llSubjects.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            llTitles.setVisibility(View.GONE);
            tvViewChart.setVisibility(View.GONE);
            getAnalysisBySubject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyValueFormatter extends ValueFormatter implements IValueFormatter {
        private final DecimalFormat mFormat = new DecimalFormat("###,###,##0");

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}
