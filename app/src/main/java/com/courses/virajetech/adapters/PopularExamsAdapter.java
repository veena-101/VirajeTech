package com.courses.virajetech.adapters;


import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.courses.virajetech.activities.CheckOutActivity;
import com.courses.virajetech.activities.ExamInstructionsActivity;
import com.courses.virajetech.R;
import com.courses.virajetech.model.PopularExams;

import java.util.List;


public class PopularExamsAdapter extends RecyclerView.Adapter<PopularExamsAdapter.ViewHolder> {

    Context context;
    List<PopularExams> popularExamsList;

    public PopularExamsAdapter(Context context,List<PopularExams> popularExamsLis){

        this.context=context;
        this.popularExamsList=popularExamsLis;
    }

    @Override
    public PopularExamsAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_popular_exams, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( PopularExamsAdapter.ViewHolder holder, int position) {

        final PopularExams popularExams = popularExamsList.get(position);

        holder.examName.setText(popularExams.getTitle());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(popularExams.getIs_paid().equals("0")){
                    Intent   intent = new Intent(context,ExamInstructionsActivity.class);
                    intent.putExtra("start_exam",popularExams.getSlug());
                    intent.putExtra("quiz_id",popularExams.getId());
                    intent.putExtra("exam_type",popularExams.getExam_type());
                    context.startActivity(intent);
                }else {

                    if(popularExams.getIs_paid().equals("0")){
                        Intent intent = new Intent(context,CheckOutActivity.class);
                        intent.putExtra("id",popularExams.getId());
                        intent.putExtra("sub_name",popularExams.getTitle());
                        intent.putExtra("validity",popularExams.getValidity());
                        intent.putExtra("cost",popularExams.getCost());
                        intent.putExtra("slug",popularExams.getSlug());
                        intent.putExtra("type","exam");
                        intent.putExtra("exam_type",popularExams.getExam_type());
                        context.startActivity(intent);
                    }else {

                        Intent   intent = new Intent(context,ExamInstructionsActivity.class);
                        intent.putExtra("start_exam",popularExams.getSlug());
                        intent.putExtra("quiz_id",popularExams.getId());
                        intent.putExtra("exam_type",popularExams.getExam_type());
                        context.startActivity(intent);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularExamsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView examName;

        public ViewHolder(View itemView) {
            super(itemView);

            examName = (TextView)itemView.findViewById(R.id.tv_popular_exam_name);
        }
    }
}
