package com.courses.virajetech.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageView imgBack, imgOldPassword, imgNewPassword, imgConfirmPassword;
    private EditText edOldPassword, edNewPassword, edConfirmPassword;
    private Button btnUpdatePassword;

    private int oldCount = 0, newCount = 0, confirmCount = 0;

    private boolean isOldVisible = false;
    private boolean isNewVisible = false;
    private boolean isConfirmVisible = false;


    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.img_toolbar_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        edOldPassword = findViewById(R.id.ed_cp_old);
        edNewPassword = findViewById(R.id.ed_cp_new);
        edConfirmPassword = findViewById(R.id.ed_cp_cnew);
        imgOldPassword = findViewById(R.id.img_cp_old);
        imgNewPassword = findViewById(R.id.img_cp_new);
        imgConfirmPassword = findViewById(R.id.img_cp_cnew);
        btnUpdatePassword = findViewById(R.id.btn_update_pwd);

        tvTitle.setText(getString(R.string.change_pwd));

        imgBack.setOnClickListener(v -> onBackPressed());

        imgNewPassword.setOnClickListener(this);
        imgConfirmPassword.setOnClickListener(this);
        imgOldPassword.setOnClickListener(this);
        btnUpdatePassword.setOnClickListener(this);

        userID = getUserID(); // Get user ID from BaseActivity or storage
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_cp_old) {

        } else if (id == R.id.img_cp_new) {

        } else if (id == R.id.img_cp_cnew) {

        } else if (id == R.id.btn_update_pwd) {
            validateAndSubmit();
        }

    }

    private boolean togglePasswordVisibility(EditText field, ImageView icon, boolean isVisible) {
        if (isVisible) {
            field.setTransformationMethod(PasswordTransformationMethod.getInstance());
            icon.setImageResource(R.drawable.eye_on); // Password hidden
        } else {
            field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            icon.setImageResource(R.drawable.eye_off); // Password visible
        }
        field.setSelection(field.getText().length());
        return !isVisible;
    }

    private void validateAndSubmit() {
        String oldPwd = edOldPassword.getText().toString().trim();
        String newPwd = edNewPassword.getText().toString().trim();
        String confirmPwd = edConfirmPassword.getText().toString().trim();

        if (oldPwd.isEmpty()) {
            edOldPassword.setError(getString(R.string.plz_enter_old_pwd));
        } else if (newPwd.isEmpty()) {
            edNewPassword.setError(getString(R.string.plz_enter_new_pwd));
        } else if (newPwd.contains(" ")) {
            edNewPassword.setError(getString(R.string.pwd_does_not_allow_spaces));
        } else if (newPwd.length() < 6) {
            edNewPassword.setError(getString(R.string.pwd_min_chars));
        } else if (confirmPwd.isEmpty()) {
            edConfirmPassword.setError(getString(R.string.plz_enter_confirm_pwd));
        } else if (!newPwd.equals(confirmPwd)) {
            edConfirmPassword.setError(getString(R.string.both_pwds_doent_match));
        } else {
            if (appState.getNetworkCheck()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", userID);
                    jsonObject.put("old_password", oldPwd);
                    jsonObject.put("password", newPwd);
                    jsonObject.put("password_confirmation", confirmPwd);
                    updatePassword(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePassword(JSONObject jsonObject) {
        Utils.showProgressDialog(this, "");
        Utils.showProgress(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Utils.CHANGE_PASSWORD, jsonObject,
                response -> {
                    Utils.dissmisProgress();
                    try {
                        int status = response.getInt("status");
                        String mssg = response.getString("message");
                        Toast.makeText(ChangePasswordActivity.this, mssg, Toast.LENGTH_SHORT).show();

                        if (status == 1) {
                            appState.logout();
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Utils.dissmisProgress();
                    Toast.makeText(ChangePasswordActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjReq);
    }
}
