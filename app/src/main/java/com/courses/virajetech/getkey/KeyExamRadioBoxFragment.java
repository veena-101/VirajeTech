package com.courses.virajetech.getkey;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.courses.virajetech.activities.TakeExamActivity;
import com.courses.virajetech.activities.TakeExamSectionWiseActivity;
import com.courses.virajetech.activities.TakeExamSectionWiseTimeActivity;
import com.courses.virajetech.adapters.KeyMultipleChoiceRadioAdapter;
import com.courses.virajetech.adapters.MultipleChoiceRadioAdapter;
import com.courses.virajetech.adapters.SpinnerLanguageAdapter;
import com.courses.virajetech.fragments.BaseFragment;
import com.courses.virajetech.model.KeyTakeExam;
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


public class KeyExamRadioBoxFragment extends BaseFragment {

    View home_fragment_view;


    RecyclerView recyclerView;
    KeyMultipleChoiceRadioAdapter adapter;
    KeyTakeExam takeExam ;
    List<Options> optionsList;
    String fromWich;
    MathJaxWebView tvQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (home_fragment_view == null) {

            home_fragment_view = inflater.inflate(R.layout.key_exam_multiple_choice,container,false);

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


        tvQuestion = (MathJaxWebView)home_fragment_view.findViewById(R.id.tv_key_choice_question) ;
        recyclerView = (RecyclerView)home_fragment_view.findViewById(R.id.rv_key_multiple_choice);


        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        Bundle bundle = this.getArguments();

        if(bundle != null) {

            takeExam =(KeyTakeExam) bundle.getSerializable("question");

            fromWich = bundle.getString("fromWich");

        }


        tvQuestion.getSettings().setJavaScriptEnabled(true);
        tvQuestion.setText(takeExam.getQuestion());

        optionsList = new ArrayList<>();

        try {
            JSONArray jsonArr = new JSONArray(takeExam.getAnswers());
            for (int i=0;i<jsonArr.length();i++){
                Gson gson = new Gson();
                Type type = new TypeToken<Options>() {}.getType();
                Options myQuestions = gson.fromJson(jsonArr.get(i).toString(), type);
                optionsList.add(myQuestions);
            }

            adapter = new KeyMultipleChoiceRadioAdapter(getActivity(),optionsList,takeExam.getId(),takeExam.getCorrect_answers(),takeExam.getUser_submitted());
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
