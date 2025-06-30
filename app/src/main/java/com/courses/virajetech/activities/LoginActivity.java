package com.courses.virajetech.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.courses.virajetech.R;
import com.courses.virajetech.utils.AppController;
import com.courses.virajetech.utils.BaseActivity;
import com.courses.virajetech.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout llCreateAccount;
    TextView tvLoginNow, tvTermsConditions, tvPrivacyPolicy, tvForgotPassword;
    EditText edUserName, edPassword;
    ImageView imgShowPwd;
    Button btnFacebook, btnGoogle;

    private static final int RC_SIGN_IN = 234;
    public GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    CallbackManager callbackManager;

    int count = 0;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        llCreateAccount = findViewById(R.id.ll_create_account);
        tvLoginNow = findViewById(R.id.tv_login_now);
        edUserName = findViewById(R.id.ed_login_username);
        edPassword = findViewById(R.id.ed_login_password);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnGoogle = findViewById(R.id.btn_google);
        imgShowPwd = findViewById(R.id.img_login_show_pwd);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_pwd);
        tvTermsConditions = findViewById(R.id.tv_terms_conditions);
        tvPrivacyPolicy = findViewById(R.id.tv_privacy_policy);

        setDrawableColor(btnFacebook, R.color.white_color);
        setDrawableColor(btnGoogle, R.color.white_color);

        llCreateAccount.setOnClickListener(this);
        tvLoginNow.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
        tvTermsConditions.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        imgShowPwd.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.offline_payment_h1))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FacebookSdk.setClientToken("@string/fb_app_id");
        FacebookSdk.sdkInitialize(getApplicationContext());

        printHashKey();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ll_create_account) {
            startActivity(new Intent(this, RegisterActivity.class));

        } else if (id == R.id.tv_login_now) {
            if (edUserName.getText().toString().isEmpty()) {
                edUserName.setError(getString(R.string.plz_enter_email));
            } else if (edPassword.getText().toString().isEmpty()) {
                edPassword.setError(getString(R.string.plz_enter_pwd));
            } else {
                if (Utils.isNetworkAvailable(this)) {
                    userLogin();
                } else {
                    Toast.makeText(this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (id == R.id.tv_privacy_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));

        } else if (id == R.id.tv_terms_conditions) {
            startActivity(new Intent(this, TermsConditionsActivity.class));

        } else if (id == R.id.btn_google) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);

        } else if (id == R.id.btn_facebook) {
            LoginButton login = new LoginButton(this);
            login.setReadPermissions(Arrays.asList("email"));
            login.performClick();
            login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        RequestData();
                    }
                }

                @Override
                public void onCancel() {}

                @Override
                public void onError(FacebookException exception) {}
            });

        } else if (id == R.id.img_login_show_pwd) {
            count++;
            if (count % 2 == 0) {
                edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imgShowPwd.setImageResource(R.drawable.eye_on);
            } else {
                edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imgShowPwd.setImageResource(R.drawable.eye_off);
            }

        } else if (id == R.id.tv_login_forgot_pwd) {
            openDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException ignored) {}
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String gEmail = acct.getEmail();
                        String gName = acct.getDisplayName();
                        socialAppLogin(gEmail, gName);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception ignored) {}
    }

    private void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (json, response) -> {
            try {
                if (json != null && response != null) {
                    String fbEmail = json.has("email") ? json.getString("email") : "";
                    String fbName = json.getString("name");
                    socialAppLogin(fbEmail, fbName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void openDialog() {
        View subView = LayoutInflater.from(this).inflate(R.layout.alert_forgot_pwd, null);
        final EditText email_id = subView.findViewById(R.id.ed_forgot_pwd);
        final Button btnSubmit = subView.findViewById(R.id.btn_forgot_pwd);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password ?");
        builder.setView(subView);
        alertDialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            if (email_id.getText().toString().isEmpty()) {
                email_id.setError("Please enter registered email id");
            } else {
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    checkEmail(email_id.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.plz_check_network_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    public void checkEmail(String strEmail) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", strEmail);
            jsonObject.put("user_type", "student");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.CHECK_EMAIL, jsonObject,
                    response -> {
                        try {
                            int status = response.getInt("status");
                            String mssg = response.getString("message");
                            Log.d("LOGIN_RESPONSE", response.toString());
                            Toast.makeText(this, mssg, Toast.LENGTH_SHORT).show();
                            if (status == 1) alertDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                error.printStackTrace();
                Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            });
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void userLogin() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", edUserName.getText().toString().trim());
            jsonObject.put("password", edPassword.getText().toString().trim());
            jsonObject.put("device_id", appState.getFCM_Id());

            Utils.showProgressDialog(this, "Logging in...");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.LOGIN, jsonObject,
                    response -> {
                        Utils.dissmisProgress();
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                JSONObject object = response.getJSONObject("user");
                                appState.setEmail(object.getString("email"));
                                appState.setPhone(object.getString("phone"));
                                appState.setUserID(object.getString("id"));
                                appState.setUserName(object.getString("name"));
                                appState.setProfilePic(object.getString("image"));

                                editor.putString("user_id", object.getString("id"));
                                editor.putString("user_name", object.getString("name"));
                                editor.putString("profilePic", object.getString("image"));
                                editor.apply();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                Utils.dissmisProgress();
                error.printStackTrace();
                Toast.makeText(this, "Network Error: " + error.toString(), Toast.LENGTH_LONG).show();
            });
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void socialAppLogin(String email, String name) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("name", name);
            jsonObject.put("device_id", appState.getFCM_Id());

            Utils.showProgressDialog(this, "Logging in...");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.SOCIAL_LOGINS, jsonObject,
                    response -> {
                        Utils.dissmisProgress();
                        try {
                            appState.setEmail(response.getString("email"));
                            appState.setUserID(response.getString("id"));
                            appState.setUserName(response.getString("name"));
                            if (response.has("image")) {
                                appState.setProfilePic(response.getString("image"));
                                editor.putString("profilePic", response.getString("image"));
                            }
                            editor.putString("user_id", response.getString("id"));
                            editor.putString("user_name", response.getString("name"));
                            editor.apply();

                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing social login response.", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                Utils.dissmisProgress();
                error.printStackTrace();
                Toast.makeText(this, "Social Login Error: " + error.toString(), Toast.LENGTH_LONG).show();
            });
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
