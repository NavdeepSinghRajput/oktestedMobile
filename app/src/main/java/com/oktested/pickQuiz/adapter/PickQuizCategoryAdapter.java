package com.oktested.pickQuiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.CategoryQuizResponse.CategoryQuizModel;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PickQuizCategoryAdapter extends RecyclerView.Adapter<PickQuizCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryQuizModel> dataItemList;
    private CategorySelected categorySelected;
    int perviousSelected = 1;
   public boolean clickable = true;

    public PickQuizCategoryAdapter(Context context, ArrayList<CategoryQuizModel> dataItemList, CategorySelected categorySelected) {
        this.categorySelected = categorySelected;
        this.context = context;
        this.dataItemList = dataItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pick_quiz_category_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (Helper.isContainValue(dataItemList.get(position).category_display)) {
            if (dataItemList.get(position).category_display.equalsIgnoreCase("Search")) {
                holder.categoryTitle.setText(dataItemList.get(position).category_display);
                holder.categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.category_search_icon));
            } else if (dataItemList.get(position).category_display.equalsIgnoreCase("Latest")) {
                holder.categoryTitle.setText(dataItemList.get(position).category_display);
                holder.categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.category_latest_icon));
            } else if (dataItemList.get(position).category_display.equalsIgnoreCase("Trending")) {
                holder.categoryTitle.setText(dataItemList.get(position).category_display);
                holder.categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.category_trending_icon));
            } else {
                holder.categoryTitle.setText(dataItemList.get(position).category_display);
                if (Helper.isContainValue(dataItemList.get(position).onexone_img)) {
                    Glide.with(context)
                            .load(dataItemList.get(position).onexone_img)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.categoryIcon);
                } else if (Helper.isContainValue(dataItemList.get(position).category_feature_img)) {
                    Glide.with(context)
                            .load(dataItemList.get(position).category_feature_img)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.categoryIcon);
                } else {
                    holder.categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
                }
            }
            if (dataItemList.get(position).selected) {
                holder.tabIndicator.setVisibility(View.VISIBLE);
                perviousSelected = position;
                categorySelected.loadCategoryData(dataItemList.get(position).category_display, dataItemList.get(position).category_slug);
            } else {
                holder.tabIndicator.setVisibility(View.INVISIBLE);
            }
        }

        setOnItemClick(holder.itemLL, position);
    }

    @Override
    public int getItemCount() {
        return dataItemList.size();
    }

    public void notifyItem(ArrayList<CategoryQuizModel> dataItemList) {
        this.dataItemList.addAll(dataItemList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLL;
        TextView categoryTitle;
        CircleImageView categoryIcon;
        View tabIndicator;

        ViewHolder(View v) {
            super(v);
            tabIndicator = v.findViewById(R.id.tabIndicator);
            categoryIcon = v.findViewById(R.id.categoryIcon);
            categoryTitle = v.findViewById(R.id.categoryTitle);
            itemLL = v.findViewById(R.id.itemLL);
        }
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            if (clickable&&perviousSelected!=position) {
                clickable = false;
                dataItemList.get(perviousSelected).selected = !dataItemList.get(perviousSelected).selected;
                dataItemList.get(position).selected = !dataItemList.get(position).selected;
                notifyDataSetChanged();
            }
        });
    }

    public interface CategorySelected {
        void loadCategoryData(String type, String slug);
    }
}