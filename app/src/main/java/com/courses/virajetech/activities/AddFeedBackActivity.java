package com.courses.virajetech.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

public class AddFeedBackActivity extends AppCompatActivity {
    private TextView tvTitle;
    private ImageView imgBack;
    private Toolbar toolbar;
    private EditText edTitle, edSubject, edDescription;
    private Button btnAddFeedBack;
    private String userID = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed_back);

        // Initialize Views
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        toolbar = findViewById(R.id.toolbar);

        edTitle = findViewById(R.id.ed_feed_back_title);
        edSubject = findViewById(R.id.ed_feed_back_subject);
        edDescription = findViewById(R.id.ed_feed_back_description);

        btnAddFeedBack = findViewById(R.id.btn_feed_back_send);
        tvTitle.setText(getString(R.string.give_feed_back));
        imgBack.setOnClickListener(v -> onBackPressed());

        btnAddFeedBack.setOnClickListener(v -> {
            String title = edTitle.getText().toString().trim();
            String subject = edSubject.getText().toString().trim();
            String description = edDescription.getText().toString().trim();

            if (title.isEmpty()) {
                edTitle.setError(getString(R.string.plz_enter_title));
            } else if (subject.isEmpty()) {
                edSubject.setError(getString(R.string.plz_add_subject));
            } else if (description.isEmpty()) {
                edDescription.setError(getString(R.string.plz_add_description));
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_id", userID);
                    jsonObject.put("title", title);
                    jsonObject.put("subject", subject);
                    jsonObject.put("description", description);

                    if (Utils.showProgress(this)) {
                        addFeedBack(jsonObject);
                    } else {
                        Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.login_now), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addFeedBack(JSONObject jsonObject) {
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);
       // Utils.showProgress(this);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Utils.ADD_FEED_BACK,
                jsonObject,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        String message = response.getString("message");
                        int status = response.getInt("status");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        if (status == 1) {
                            onBackPressed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.login_now), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(this, getString(R.string.vernsion), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
        queue.add(request);
    }
}
