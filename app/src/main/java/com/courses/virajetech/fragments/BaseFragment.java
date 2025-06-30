package com.courses.virajetech.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.courses.virajetech.utils.AppController;
import com.courses.virajetech.utils.Utils;

import org.jsoup.Jsoup;

public class BaseFragment extends Fragment {

    public AppController appState;
    public Response.ErrorListener errorListener;
    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            appState = ((AppController) getActivity().getApplicationContext());
            sharedpreferences = getActivity().getSharedPreferences(Utils.MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
        }

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
                Utils.dissmisProgress();
            }
        };
    }

    public String htmlToString(String htmlString) {
        return Jsoup.parse(htmlString).text();
    }

    AlertDialog.Builder builder;

    public void setHint(String hint) {
        if (getActivity() != null) {
            builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(hint)
                    .setTitle("Hint")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void hideSoftKeyboard(View view) {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
