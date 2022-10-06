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
import com.oktested.home.model.CategoryQuizResponse.CategoryQuizModel;
import com.oktested.home.model.QuizListModel;
import com.oktested.quizDetail.ui.QuizCategoryDetailActivity;
import com.oktested.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryQuizAdapter extends RecyclerView.Adapter<CategoryQuizAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryQuizModel> dataItemList;
    private String sectionTitle, offset, screenName;

    public CategoryQuizAdapter(Context context, ArrayList<CategoryQuizModel> dataItemList, String sectionTitle, String offset, String screenName) {
        this.context = context;
        this.dataItemList = dataItemList;
        this.sectionTitle = sectionTitle;
        this.offset = offset;
        this.screenName = screenName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view_item, parent, false);
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

            if (Helper.isContainValue(dataItemList.get(position).category_display)) {
                holder.titleTV.setText(dataItemList.get(position).category_display);
            }

            if (Helper.isContainValue(dataItemList.get(position).onexone_img)) {
                Glide.with(context)
                        .load(dataItemList.get(position).onexone_img)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.quizCategoryIV);
            } else if (Helper.isContainValue(dataItemList.get(position).category_feature_img)) {
                Glide.with(context)
                        .load(dataItemList.get(position).category_feature_img)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.quizCategoryIV);
            } else {
                holder.quizCategoryIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
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
        TextView titleTV;
        CircleImageView quizCategoryIV;
        LinearLayout itemLL;
        CardView viewAllCV;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            quizCategoryIV = v.findViewById(R.id.quizCategoryIV);
            titleTV = v.findViewById(R.id.titleTV);
            viewAllCV = v.findViewById(R.id.viewAllCV);
        }
    }

    private void setOnViewAllClick(CardView viewAllCV) {
        viewAllCV.setOnClickListener(view -> {
            Intent intent = new Intent(context, AllQuizActivity.class);
            intent.putExtra("offset", offset);
            intent.putExtra("screenName", screenName);
            intent.putExtra("sectionTitle", sectionTitle);
            intent.putParcelableArrayListExtra("dataItemList", dataItemList);
            intent.putExtra("type", "categories");
            context.startActivity(intent);
        });
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, QuizCategoryDetailActivity.class);
            intent.putExtra("categorySlug", dataItemList.get(position).category_slug);
            context.startActivity(intent);
        });
    }
}