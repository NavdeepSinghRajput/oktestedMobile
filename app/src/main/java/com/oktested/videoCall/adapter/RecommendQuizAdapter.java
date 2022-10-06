package com.oktested.videoCall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.QuizListModel;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class RecommendQuizAdapter extends RecyclerView.Adapter<RecommendQuizAdapter.ViewHolder>  {

    private Context context;
    private ArrayList<QuizListModel> dataItemList;
    private Quiz quiz;

    public RecommendQuizAdapter(Context context, ArrayList<QuizListModel> dataItemList, Quiz quiz) {
        this.context = context;
        this.dataItemList = dataItemList;
        this.quiz = quiz;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_category_detail_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(dataItemList.get(position).cat_display.get(0).category_display)) {
            holder.topicTV.setText(dataItemList.get(position).cat_display.get(0).category_display);
        }
        if (Helper.isContainValue(dataItemList.get(position).title)) {
            holder.titleTV.setText(dataItemList.get(position).title);
        }
        if (Helper.isContainValue(dataItemList.get(position).feature_img_1x1)) {
            Glide.with(context)
                    .load(dataItemList.get(position).feature_img_1x1)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.videoThumbIV);
        } else {
            holder.videoThumbIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        setOnItemClick(holder.itemLL, position);
    }

    public void setMoreVideosData(ArrayList<QuizListModel> dataItemList) {
        this.dataItemList.addAll(dataItemList);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return dataItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void clearAll() {
        dataItemList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTV;
        TextView titleTV;
        ImageView videoThumbIV;
        CardView viewAllCV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            viewAllCV = v.findViewById(R.id.viewAllCV);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            topicTV = v.findViewById(R.id.topicTV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            quiz.recommendQuiz(dataItemList.get(position).article_id);
        });
    }

    public interface Quiz {
        void recommendQuiz(String articleId);
    }
}