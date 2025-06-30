package com.courses.virajetech.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.courses.virajetech.R;
import com.courses.virajetech.fragments.ExamMatchFragment;


import java.io.UnsupportedEncodingException;
import java.util.List;

public class MatchRightAdapter extends RecyclerView.Adapter<MatchRightAdapter.ViewHolder> {

    Context context;
    List<String> list;

    ExamMatchFragment fragment ;

    public MatchRightAdapter(Context context, List<String> list, ExamMatchFragment fragment){

        this.context = context ;
        this.list = list ;
        this.fragment = fragment ;

    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_match_right, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        try {
            String   name = new String(list.get(position).getBytes("UTF-16"), "UTF-16");

            holder.tvListOptions.setText(name);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        holder.edAns.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    fragment.addCheckBoxValues(holder.edAns.getText().toString().trim());
                    hideSoftKeyboard(holder.edAns);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvListOptions;
        EditText edAns;

        public ViewHolder(View itemView) {
            super(itemView);

            tvListOptions = (TextView)itemView.findViewById(R.id.tv_row_match_right);
            edAns = (EditText) itemView.findViewById(R.id.ed_row_match_ans);

        }
    }

    public void hideSoftKeyboard(View view) {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
