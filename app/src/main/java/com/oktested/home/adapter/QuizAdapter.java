package com.oktested.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.allQuiz.AllQuizActivity;
import com.oktested.home.holder.QuizViewHolder;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.QuizStructureListModel;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.quizDetail.ui.QuizCategoryDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;

import java.util.ArrayList;
import java.util.Map;

public class QuizAdapter extends RecyclerView.Adapter<QuizViewHolder> {

    private Context context;
    private ArrayList<QuizStructureListModel> structureListModel;
    private Map<Integer, Object> sectionMap;
    private ArrayList<Integer> moreDetailsCountAL;
    private int count = 0;

    public QuizAdapter(Context context, ArrayList<QuizStructureListModel> structureListModel, Map<Integer, Object> sectionMap, ArrayList<Integer> moreDetailsCountAL) {
        this.context = context;
        this.structureListModel = structureListModel;
        this.sectionMap = sectionMap;
        this.moreDetailsCountAL = moreDetailsCountAL;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AppConstants.SECTION_QUIZ_TRENDING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_trending, parent, false);
                return new QuizViewHolder(view, viewType);

            case AppConstants.SECTION_QUIZ_LATEST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_latest, parent, false);
                return new QuizViewHolder(view, viewType);

            case AppConstants.SECTION_QUIZ_CATEGORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_category, parent, false);
                return new QuizViewHolder(view, viewType);

            case AppConstants.SECTION_QUIZ_CATEGORY_DETAILS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_category_details, parent, false);
                return new QuizViewHolder(view, viewType);

            case AppConstants.SECTION_UNKNOWN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unknown_layout, parent, false);
                return new QuizViewHolder(view, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizStructureListModel listModel = structureListModel.get(position);
        if (listModel != null) {
            switch (listModel.section_type) {
                case AppConstants.SECTION_QUIZ_TRENDING_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_QUIZ_TRENDING)) {
                        holder.quizTrendingRL.setVisibility(View.VISIBLE);
                        holder.quizTrendingNameTV.setText(structureListModel.get(position).value.section_title);
                        loadTrendingSection(holder, (TrendingQuizResponse) sectionMap.get(AppConstants.SECTION_QUIZ_TRENDING), structureListModel.get(position).value.section_title);
                    } else {
                        holder.quizTrendingRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_QUIZ_LATEST_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_QUIZ_LATEST)) {
                        holder.quizLatestRL.setVisibility(View.VISIBLE);
                        holder.quizLatestTV.setText(structureListModel.get(position).value.section_title);
                        loadLatestSection(holder, (LatestQuizResponse) sectionMap.get(AppConstants.SECTION_QUIZ_LATEST), structureListModel.get(position).value.section_title);
                    } else {
                        holder.quizLatestRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_QUIZ_CATEGORY)) {
                        holder.quizCategoryRL.setVisibility(View.VISIBLE);
                        holder.quizCategoryHeaderTV.setText(structureListModel.get(position).value.section_title);
                        loadCategorySection(holder, (CategoryQuizResponse) sectionMap.get(AppConstants.SECTION_QUIZ_CATEGORY), structureListModel.get(position).value.section_title);
                    } else {
                        holder.quizCategoryRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT:
                    if (moreDetailsCountAL != null && moreDetailsCountAL.size() > 0) {
                        for (int i = count; i < moreDetailsCountAL.size(); i++) {
                            if (sectionMap.containsKey(moreDetailsCountAL.get(i))) {
                                loadMoreShowsSection(holder, (CategoryDetailQuizResponse) sectionMap.get(moreDetailsCountAL.get(i)));
                                count++;
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    public void notifyItem() {
        count = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (structureListModel.get(position).section_type) {
            case AppConstants.SECTION_QUIZ_TRENDING_LAYOUT:
                return AppConstants.SECTION_QUIZ_TRENDING;

            case AppConstants.SECTION_QUIZ_LATEST_LAYOUT:
                return AppConstants.SECTION_QUIZ_LATEST;

            case AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT:
                return AppConstants.SECTION_QUIZ_CATEGORY;

            case AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT:
                return AppConstants.SECTION_QUIZ_CATEGORY_DETAILS;

            default:
                return AppConstants.SECTION_UNKNOWN;
        }
    }

    @Override
    public int getItemCount() {
        return structureListModel.size();
    }

    private void loadTrendingSection(QuizViewHolder holder, TrendingQuizResponse trendingQuizResponse, String sectionTitle) {
        if (holder != null && trendingQuizResponse.data != null && trendingQuizResponse.data.size() > 0) {
            holder.quizTrendingRV.setHasFixedSize(true);
            holder.quizTrendingRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.quizTrendingRV.setItemAnimator(new DefaultItemAnimator());
            LatestQuizAdapter latestQuizAdapter = new LatestQuizAdapter(context, trendingQuizResponse.data, sectionTitle, trendingQuizResponse.next_offset, AppConstants.SECTION_QUIZ_TRENDING_LAYOUT);
            holder.quizTrendingRV.setAdapter(latestQuizAdapter);

            holder.quizTrendingRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllQuizActivity.class);
                intent.putExtra("offset", trendingQuizResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_QUIZ_TRENDING_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putExtra("type", "");
                intent.putParcelableArrayListExtra("dataItemList", trendingQuizResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadLatestSection(QuizViewHolder holder, LatestQuizResponse latestQuizResponse, String sectionTitle) {
        if (holder != null && latestQuizResponse.data != null && latestQuizResponse.data.size() > 0) {
            holder.quizLatestRV.setHasFixedSize(true);
            holder.quizLatestRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.quizLatestRV.setItemAnimator(new DefaultItemAnimator());
            LatestQuizAdapter latestQuizAdapter = new LatestQuizAdapter(context, latestQuizResponse.data, sectionTitle, latestQuizResponse.next_offset, AppConstants.SECTION_QUIZ_LATEST_LAYOUT);
            holder.quizLatestRV.setAdapter(latestQuizAdapter);

            holder.quizLatestRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllQuizActivity.class);
                intent.putExtra("offset", latestQuizResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_QUIZ_LATEST_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putExtra("type", "");
                intent.putParcelableArrayListExtra("dataItemList", latestQuizResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadCategorySection(QuizViewHolder holder, CategoryQuizResponse categoryQuizResponse, String sectionTitle) {
        if (holder != null && categoryQuizResponse.data != null && categoryQuizResponse.data.size() > 0) {
            holder.quizCategoryRV.setHasFixedSize(true);
            holder.quizCategoryRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.quizCategoryRV.setItemAnimator(new DefaultItemAnimator());
            CategoryQuizAdapter categoryQuizAdapter = new CategoryQuizAdapter(context, categoryQuizResponse.data, sectionTitle, categoryQuizResponse.next_offset, AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT);
            holder.quizCategoryRV.setAdapter(categoryQuizAdapter);

            holder.quizCategoryRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllQuizActivity.class);
                intent.putExtra("offset", categoryQuizResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_QUIZ_CATEGORY_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", categoryQuizResponse.data);
                intent.putExtra("type", "categories");
                context.startActivity(intent);
            });
        }
    }

    private void loadMoreShowsSection(QuizViewHolder holder, CategoryDetailQuizResponse categoryDetailQuizResponse) {
        if (holder != null && categoryDetailQuizResponse.data != null && categoryDetailQuizResponse.data.size() > 0) {
            holder.quizMoreDetailsArrowIV.setVisibility(View.VISIBLE);
            String sectionTitle = "";
            if (Helper.isContainValue(categoryDetailQuizResponse.cat_data.category_display)) {
                sectionTitle = categoryDetailQuizResponse.cat_data.category_display;
                holder.quizNameTV.setText(sectionTitle);
            }
            holder.quizDetailRV.setHasFixedSize(true);
            holder.quizDetailRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.quizDetailRV.setItemAnimator(new DefaultItemAnimator());
            CategoryQuizDetailAdapter categoryQuizDetailAdapter = new CategoryQuizDetailAdapter(context, categoryDetailQuizResponse.data, sectionTitle, categoryDetailQuizResponse.next_offset, AppConstants.SECTION_QUIZ_CATEGORY_DETAILS_LAYOUT);
            holder.quizDetailRV.setAdapter(categoryQuizDetailAdapter);

            holder.quizDetailRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, QuizCategoryDetailActivity.class);
                intent.putExtra("categorySlug", categoryDetailQuizResponse.cat_data.category_slug);
                context.startActivity(intent);
            });
        }
    }
}