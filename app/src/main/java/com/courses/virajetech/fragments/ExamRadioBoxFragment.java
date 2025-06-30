package com.courses.virajetech.fragments;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.activities.AddFeedBackActivity;
import com.courses.virajetech.activities.TakeExamActivity;
import com.courses.virajetech.activities.TakeExamSectionWiseActivity;
import com.courses.virajetech.activities.TakeExamSectionWiseTimeActivity;
import com.courses.virajetech.adapters.MultipleChoiceRadioAdapter;
import com.courses.virajetech.adapters.SpinnerLanguageAdapter;
import com.courses.virajetech.model.Options;
import com.courses.virajetech.model.TakeExam;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.courses.virajetech.view.MathJaxWebView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ExamRadioBoxFragment extends BaseFragment {

    View home_fragment_view;

    TextView tvMarkForReview;
    ImageView markForReview,bookMark,imgHint;
    LinearLayout llMarkForReview;
    RecyclerView recyclerView;
    MultipleChoiceRadioAdapter adapter;

    TakeExam takeExam ;

    List<Options> optionsList;
    int markReviewCount=1;

    boolean setCheckedMarkForReview;

    String fromWich;

    Spinner sprLanguage;
    List<String> languagesList;
    int languageSelected=0;
  

    MathJaxWebView mathJaxWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (home_fragment_view == null) {

            home_fragment_view = inflater.inflate(R.layout.take_exam_multiple_choice,container,false);

            // Initialise your layout here

        } else {
            ((ViewGroup) home_fragment_view.getParent()).removeView(home_fragment_view);
        }



       initUI();

        return home_fragment_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public void initUI(){

        
        recyclerView = (RecyclerView)home_fragment_view.findViewById(R.id.rv_multiple_choice);
        llMarkForReview = (LinearLayout)home_fragment_view.findViewById(R.id.ll_take_exam_mark_for_review);
        markForReview = (ImageView)home_fragment_view.findViewById(R.id.img_take_exam_mark_for_review);
        tvMarkForReview = (TextView)home_fragment_view.findViewById(R.id.tv_take_exam_mark_for_review) ;
        bookMark = (ImageView)home_fragment_view.findViewById(R.id.img_take_exam_bookmark);
        sprLanguage = (Spinner)home_fragment_view.findViewById(R.id.spr_select_language);
        mathJaxWebView = (MathJaxWebView)home_fragment_view.findViewById(R.id.webView) ;
        imgHint = (ImageView)home_fragment_view.findViewById(R.id.img_take_exam_hint);


        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        Bundle bundle = this.getArguments();

        if(bundle != null) {

            takeExam =(TakeExam) bundle.getSerializable("question");

            fromWich = bundle.getString("fromWich");
            languagesList = bundle.getStringArrayList("languages");

        }

        if(languagesList.size()>1){

            sprLanguage.setVisibility(View.VISIBLE);

            SpinnerLanguageAdapter adapter = new SpinnerLanguageAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item,languagesList);
            sprLanguage.setAdapter(adapter);

        }


        if(takeExam.getQuestion_tags().getIs_bookmarked()==1){
            bookMark.setColorFilter(ContextCompat.getColor(getActivity(),R.color.exams_bg));
        }else {
            bookMark.setColorFilter(ContextCompat.getColor(getActivity(),R.color.grey_color));
        }

        if(setCheckedMarkForReview){
            markForReview.setColorFilter(ContextCompat.getColor(getActivity(),R.color.analysis_bg_primary));
            tvMarkForReview.setTextColor(ContextCompat.getColor(getActivity(),R.color.analysis_bg_primary));

        }else {
            markForReview.setColorFilter(ContextCompat.getColor(getActivity(),R.color.grey_color));
            tvMarkForReview.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_color));

        }

        bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addBookMark();
            }
        });

        llMarkForReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markReviewCount++;

                if(markReviewCount%2==0){
                    setCheckedMarkForReview=true;
                    markForReview.setColorFilter(ContextCompat.getColor(getActivity(),R.color.analysis_bg_primary));
                    tvMarkForReview.setTextColor(ContextCompat.getColor(getActivity(),R.color.analysis_bg_primary));


                    if(fromWich.equals("NSNT")){
                        ((TakeExamActivity)getActivity()).changeTabTextColor("add");
                    }else if(fromWich.equals("SNT")){
                        ((TakeExamSectionWiseActivity)getActivity()).changeTabTextColor("add");
                    }
                    else if(fromWich.equals("ST")){
                        ((TakeExamSectionWiseTimeActivity)getActivity()).changeTabTextColor("add");
                    }

                    Toast.makeText(getActivity(), "Marked for review ", Toast.LENGTH_SHORT).show();

                }else {
                    setCheckedMarkForReview=false;
                    markForReview.setColorFilter(ContextCompat.getColor(getActivity(),R.color.grey_color));
                    tvMarkForReview.setTextColor(ContextCompat.getColor(getActivity(),R.color.grey_color));

                    if(fromWich.equals("NSNT")){
                        ((TakeExamActivity)getActivity()).changeTabTextColor("remove");
                    }else if(fromWich.equals("SNT")){
                        ((TakeExamSectionWiseActivity)getActivity()).changeTabTextColor("remove");
                    }
                    else if(fromWich.equals("ST")){
                        ((TakeExamSectionWiseTimeActivity)getActivity()).changeTabTextColor("remove");
                    }
                   // Toast.makeText(getActivity(), "Marked for review is removed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(takeExam.getHint()!=null && !takeExam.getHint().equals("")){

            imgHint.setVisibility(View.VISIBLE);

        }

        imgHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint(takeExam.getHint());
            }
        });

        mathJaxWebView.getSettings().setJavaScriptEnabled(true);
        mathJaxWebView.setText(takeExam.getQuestion());

       
        optionsList = new ArrayList<>();

        try {
            JSONArray jsonArr = new JSONArray(takeExam.getAnswers());
            for (int i=0;i<jsonArr.length();i++){
                Gson gson = new Gson();
                Type type = new TypeToken<Options>() {}.getType();
                Options myQuestions = gson.fromJson(jsonArr.get(i).toString(), type);
                optionsList.add(myQuestions);
            }
            adapter = new MultipleChoiceRadioAdapter(getActivity(),optionsList,takeExam.getId(),fromWich,0);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        sprLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageSelected = position;

                if (position == 1) {
                    if (takeExam.getQuestion_l2() != null && !takeExam.getQuestion_l2().equals("")) {
                        mathJaxWebView.setText(takeExam.getQuestion_l2());
                        adapter = new MultipleChoiceRadioAdapter(getActivity(), optionsList, takeExam.getId(), fromWich, position);
                        recyclerView.setAdapter(adapter);
                    }
                } else {

                    mathJaxWebView.setText(takeExam.getQuestion());
                    adapter = new MultipleChoiceRadioAdapter(getActivity(), optionsList,takeExam.getId(), fromWich, position);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void addBookMark(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", ((BaseActivity)getActivity()).getUserID());
            jsonObject.put("item_id",takeExam.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(getActivity(),"");
        Utils.showProgress(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Utils.BOOK_MARK_SAVE_REMOVE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dissmisProgress();
                        String mssg,amountPay,discount;
                        int status;

                        try {
                            mssg = response.getString("message");
                            status = response.getInt("status");
                            if(status==1){
                                takeExam.getQuestion_tags().setIs_bookmarked(1);
                                bookMark.setColorFilter(ContextCompat.getColor(getActivity(),R.color.exams_bg));
                                Toast.makeText(getActivity(), "added to book mark list", Toast.LENGTH_SHORT).show();
                            }else {
                                takeExam.getQuestion_tags().setIs_bookmarked(0);
                                bookMark.setColorFilter(ContextCompat.getColor(getActivity(),R.color.grey_color));
                                Toast.makeText(getActivity(), "removed from book mark list", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, errorListener) {
        };
        queue.add(jsonObjReq);

    }
}
