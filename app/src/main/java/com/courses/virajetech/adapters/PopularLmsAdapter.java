package com.courses.virajetech.adapters;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.courses.virajetech.activities.CheckOutActivity;
import com.courses.virajetech.activities.LMSSeriesCategoryListActivity;
import com.courses.virajetech.R;
import com.courses.virajetech.model.CategoryListLMS;

import java.util.List;

public class PopularLmsAdapter extends RecyclerView.Adapter<PopularLmsAdapter.ViewHolder> {

    Context context;
    List<CategoryListLMS> popularLMSList;

    public PopularLmsAdapter(Context context,List<CategoryListLMS> popularLMSList){

        this.context=context;
        this.popularLMSList=popularLMSList;
    }

    @Override
    public PopularLmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_popular_lms, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( PopularLmsAdapter.ViewHolder holder, int position) {

        final CategoryListLMS popularLMS = popularLMSList.get(position);

        holder.tvTitle.setText(popularLMS.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(popularLMS.getIs_paid().equals("0")){
                    Intent intent = new Intent(context, LMSSeriesCategoryListActivity.class);
                    intent.putExtra("lms_list",popularLMS);
                    context.startActivity(intent);
                }else {

                    if(popularLMS.getIs_purchased().equals("0")){

                        Intent intent = new Intent(context, CheckOutActivity.class);
                        intent.putExtra("id",popularLMS.getId());
                        intent.putExtra("sub_name",popularLMS.getTitle());
                        intent.putExtra("validity",popularLMS.getValidity());
                        intent.putExtra("cost",popularLMS.getCost());
                        intent.putExtra("slug",popularLMS.getSlug());
                        intent.putExtra("type","lms");
                        intent.putExtra("exam_type","popular_lms");
                        context.startActivity(intent);

                    }else {
                        Intent intent = new Intent(context, LMSSeriesCategoryListActivity.class);
                        intent.putExtra("lms_list",popularLMS);
                        context.startActivity(intent);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularLMSList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_popular_lms_name);
        }
    }
}
