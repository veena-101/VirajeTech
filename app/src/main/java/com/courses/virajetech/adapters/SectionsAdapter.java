package com.courses.virajetech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.courses.virajetech.R;
import com.courses.virajetech.activities.TakeExamSectionWiseActivity;
import com.courses.virajetech.getkey.KeyTakeExamSectionWiseActivity;
import com.courses.virajetech.model.Sections;


import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {

    List<Sections> sectionsList ;
    Context context ;
    int row_index ;

    String fromWhich;

    public SectionsAdapter(Context context,List<Sections>list,String from){

        this.context=context;
        this.sectionsList=list;
        this.fromWhich = from ;
    }

    @Override
    public SectionsAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sections, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Sections sections = sectionsList.get(position);

        holder.tvTitle.setText(sections.getSection_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                row_index = position;
                notifyDataSetChanged();

                if(fromWhich.equals("exam")){
                    ((TakeExamSectionWiseActivity)context).sectionSelected(sections.getQuestions());
                }
              else {
                    ((KeyTakeExamSectionWiseActivity)context).sectionSelected(sections.getQuestions());
                }
            }
        });

        if(row_index==position){
            holder.tvTitle.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_color));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white_color));
        }
        else
        {
            holder.tvTitle.setBackgroundColor(ContextCompat.getColor(context,R.color.white_color));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.grey_color));
        }

    }

    @Override
    public int getItemCount() {
        return sectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_row_section_name);
        }
    }
}
