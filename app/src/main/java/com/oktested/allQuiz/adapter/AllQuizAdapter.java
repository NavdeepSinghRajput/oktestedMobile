package com.oktested.allQuiz.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.oktested.quizDetail.ui.QuizDetailActivity;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class AllQuizAdapter extends RecyclerView.Adapter<AllQuizAdapter.ViewHolder> {

    private Context context;
    private ArrayList<QuizListModel> dataItemList;

    public AllQuizAdapter(Context context, ArrayList<QuizListModel> dataItemList) {
        this.context = context;
        this.dataItemList = dataItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_quiz_type_item, parent, false);
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
                    .into(holder.quizThumbIV);
        } else {
            holder.quizThumbIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }
        setOnItemClick(holder.itemLL, position);
    }

    @Override
    public int getItemCount() {
        return dataItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTV;
        TextView titleTV;
        ImageView quizThumbIV;
        CardView viewAllCV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            viewAllCV = v.findViewById(R.id.viewAllCV);
            quizThumbIV = v.findViewById(R.id.quizThumbIV);
            topicTV = v.findViewById(R.id.topicTV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    public void setMoreVideosData(ArrayList<QuizListModel> dataItemList) {
        this.dataItemList.addAll(dataItemList);
        notifyDataSetChanged();
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, QuizDetailActivity.class);
            intent.putExtra("articleId", dataItemList.get(position).article_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
}