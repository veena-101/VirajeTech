package com.courses.virajetech.getkey;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.courses.virajetech.adapters.KeyBlankAdapter;
import com.courses.virajetech.adapters.SpinnerLanguageAdapter;
import com.courses.virajetech.fragments.BaseFragment;
import com.courses.virajetech.model.KeyTakeExam;
import com.courses.virajetech.model.TakeExam;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.courses.virajetech.view.MathJaxWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class KeyExamBlankFragment extends BaseFragment {

    View home_fragment_view;

    KeyTakeExam takeExam ;

    String correctAns,userAns;

    MathJaxWebView tvQuestion;

    RecyclerView recyclerView;
    KeyBlankAdapter adapter ;

    List<String> correctAnsList;
    List<String> userAnsList;


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        home_fragment_view = inflater.inflate(R.layout.key_exam_blank,container,false);

        initUI();

        return home_fragment_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void initUI(){

        tvQuestion = (MathJaxWebView)home_fragment_view.findViewById(R.id.tv_key_blank_question);
        recyclerView = (RecyclerView)home_fragment_view.findViewById(R.id.rv_key_blank);


        Bundle bundle = this.getArguments();

        if(bundle != null) {
            takeExam =(KeyTakeExam) bundle.getSerializable("question");

        }


        tvQuestion.getSettings().setJavaScriptEnabled(true);
        tvQuestion.setText(takeExam.getQuestion());



        try {
            correctAnsList = new ArrayList<>();
            userAnsList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(takeExam.getCorrect_answers());

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                correctAns = jsonObject.getString("answer");
                correctAnsList.add(correctAns);

            }

            if(takeExam.getUser_submitted()!=null && !takeExam.getUser_submitted().equals("")){
                JSONArray userAnsArray = new JSONArray(takeExam.getUser_submitted());

                for (int j=0;j<userAnsArray.length();j++){

                    JSONObject jsonObject = userAnsArray.getJSONObject(j);
                    userAns = jsonObject.getString("answer");
                    userAnsList.add(userAns);


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new KeyBlankAdapter(getActivity(),correctAnsList,userAnsList);
        recyclerView.setAdapter(adapter);


    }


}
