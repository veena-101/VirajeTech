package com.courses.virajetech.activities;


import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.adapters.NotificationsAdapter;
import com.courses.virajetech.model.Notifications;
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

public class NotificationsActivity extends BaseActivity {


    RecyclerView recyclerView;
    TextView noNotifications,tvTitle;
    ImageView imgBack;
    Toolbar toolbar;

    List<Notifications> notificationsList;
    NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
     //   setStatusBarColor(this,R.color.notifications_bg);

        recyclerView = (RecyclerView)findViewById(R.id.rv_notifications);
        noNotifications = (TextView)findViewById(R.id.tv_no_notifications);
        imgBack = (ImageView)findViewById(R.id.img_toolbar_back);
        tvTitle = (TextView)findViewById(R.id.tv_toolbar_title);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tvTitle.setText(getString(R.string.notifications));
       // setToolBarBackgroundColor(toolbar,this,R.color.notifications_bg);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        getNotifications();

    }

    public void getNotifications(){

        notificationsList = new ArrayList<>();
        Utils.showProgressDialog(this,"");
        Utils.showProgress(this);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Utils.NOTIFICATIONS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dissmisProgress();
                        int status;
                        JSONArray jsonArray;
                        Log.e("no_resp",response+"");
                        try {
                            status = response.getInt("status");
                            if(status==1){

                                jsonArray = response.getJSONArray("records");

                                for (int i=0;i<jsonArray.length();i++){
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<Notifications>() {}.getType();
                                    Notifications myQuestions = gson.fromJson(jsonArray.get(i).toString(), type);
                                    notificationsList.add(myQuestions);
                                }

                                if(notificationsList.size()>0){
                                    noNotifications.setVisibility(View.GONE);
                                    adapter = new NotificationsAdapter(NotificationsActivity.this,notificationsList);
                                    recyclerView.setAdapter(adapter);
                                }else {
                                    noNotifications.setVisibility(View.VISIBLE);
                                }


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
