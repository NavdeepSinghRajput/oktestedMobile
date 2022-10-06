package com.oktested.home.adapter;

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
import com.oktested.allQuiz.AllQuizActivity;
import com.oktested.home.model.QuizListModel;
import com.oktested.quizDetail.ui.QuizDetailActivity;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class CategoryQuizDetailAdapter extends RecyclerView.Adapter<CategoryQuizDetailAdapter.ViewHolder> {

    private Context context;
    private ArrayList<QuizListModel> dataItemList;
    private String sectionTitle, offset, screenName;

    public CategoryQuizDetailAdapter(Context context, ArrayList<QuizListModel> dataItemList, String sectionTitle, String offset, String screenName) {
        this.context = context;
        this.dataItemList = dataItemList;
        this.sectionTitle = sectionTitle;
        this.offset = offset;
        this.screenName = screenName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_category_detail_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == dataItemList.size()) {
            holder.viewAllCV.setVisibility(View.VISIBLE);
            holder.itemLL.setVisibility(View.GONE);
        } else {
            holder.viewAllCV.setVisibility(View.GONE);
            holder.itemLL.setVisibility(View.VISIBLE);

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
        }

        setOnViewAllClick(holder.viewAllCV);
        setOnItemClick(holder.itemLL, position);
    }

    @Override
    public int getItemCount() {
        return dataItemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    private void setOnViewAllClick(CardView viewAllCV) {
        viewAllCV.setOnClickListener(view -> {
            Intent intent = new Intent(context, AllQuizActivity.class);
            intent.putExtra("offset", offset);
            intent.putExtra("screenName", screenName);
            intent.putExtra("sectionTitle", sectionTitle);
            intent.putParcelableArrayListExtra("dataItemList", dataItemList);
            intent.putExtra("type", "");
            context.startActivity(intent);
        });
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, QuizDetailActivity.class);
            intent.putExtra("articleId", dataItemList.get(position).article_id);
            context.startActivity(intent);
        });
    }
}