package com.courses.virajetech.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    LinearLayout llAccount;
    EditText edName, edUserName, edEmail, edPassword, edConfirmPassword;
    TextView tvRegisterNow, tvTermsConditions, tvPrivacyPolicy;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ImageView imgShowPassword, imgShowCPassword;
    int pCount = 0, cCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        llAccount = findViewById(R.id.ll_have_account);
        tvRegisterNow = findViewById(R.id.tv_register_now);
        edName = findViewById(R.id.ed_register_name);
        edUserName = findViewById(R.id.ed_register_mobile);
        edEmail = findViewById(R.id.ed_register_email);
        edPassword = findViewById(R.id.ed_register_password);
        edConfirmPassword = findViewById(R.id.ed_register_pwd_confirm);
        imgShowCPassword = findViewById(R.id.img_register_show_cpwd);
        imgShowPassword = findViewById(R.id.img_register_show_pwd);
        tvTermsConditions = findViewById(R.id.tv_terms_conditions);
        tvPrivacyPolicy = findViewById(R.id.tv_privacy_policy);

        llAccount.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
        tvTermsConditions.setOnClickListener(this);
        imgShowPassword.setOnClickListener(this);
        imgShowCPassword.setOnClickListener(this);
        tvRegisterNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ll_have_account) {
            startActivity(new Intent(this, LoginActivity.class));

        } else if (id == R.id.tv_register_now) {
            String name = edName.getText().toString().trim();
            String username = edUserName.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String confirmPassword = edConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.matches(emailPattern)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("username", username);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("device_id", appState.getFCM_Id());

                registerUser(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.tv_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        } else if (id == R.id.tv_terms_conditions) {
            startActivity(new Intent(this, TermsConditionsActivity.class));
        } else if (id == R.id.img_register_show_pwd) {
            pCount++;
            edPassword.setTransformationMethod((pCount % 2 == 0) ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
            imgShowPassword.setImageResource((pCount % 2 == 0) ? R.drawable.eye_on : R.drawable.eye_off);
        } else if (id == R.id.img_register_show_cpwd) {
            cCount++;
            edConfirmPassword.setTransformationMethod((cCount % 2 == 0) ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
            imgShowCPassword.setImageResource((cCount % 2 == 0) ? R.drawable.eye_on : R.drawable.eye_off);
        }
    }
    public void registerUser(JSONObject jsonObject) {
        Utils.showProgressDialog(this, "Registering...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.REGISTER, jsonObject,
                response -> {
                    Utils.dissmisProgress();
                    Log.d("REGISTER_RESPONSE", response.toString());
                    try {
                        int status = response.getInt("status");
                        String message = response.getString("message");
                        if (status == 1) {
                            Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Log.e("REGISTER_ERROR", error.toString());

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e("REGISTER_ERROR_BODY", body);
                        Toast.makeText(this, "Server error: " + body, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
