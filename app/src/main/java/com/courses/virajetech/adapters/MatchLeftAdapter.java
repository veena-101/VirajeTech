package com.courses.virajetech.adapters;


import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.courses.virajetech.R;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MatchLeftAdapter extends RecyclerView.Adapter<MatchLeftAdapter.ViewHolder> {

    Context context;
    List<String> list;

    public MatchLeftAdapter(Context context,List<String> list){

        this.context = context ;
        this.list = list ;


    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_match_left, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {

        try {
            String   name = new String(list.get(position).getBytes("UTF-16"), "UTF-16");

            holder.tvListOptions.setText(name);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvListOptions;

        public ViewHolder(View itemView) {
            super(itemView);

            tvListOptions = (TextView)itemView.findViewById(R.id.tv_row_match_left);
        }
    }
}
