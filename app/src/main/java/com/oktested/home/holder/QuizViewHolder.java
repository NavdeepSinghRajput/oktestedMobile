package com.oktested.home.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.utils.AppConstants;

public class QuizViewHolder extends RecyclerView.ViewHolder {

    public TextView quizNameTV, quizTrendingNameTV, quizCategoryHeaderTV, quizLatestTV;
    public ImageView quizMoreDetailsArrowIV;
    public RecyclerView quizDetailRV, quizTrendingRV, quizCategoryRV, quizLatestRV;
    public RelativeLayout quizTrendingRL, quizLatestRL, quizCategoryRL, quizDetailRL;

    public QuizViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        switch (viewType) {
            case AppConstants.SECTION_QUIZ_CATEGORY_DETAILS:
                quizDetailRV = itemView.findViewById(R.id.quizDetailRV);
                quizDetailRL = itemView.findViewById(R.id.quizDetailRL);
                quizNameTV = itemView.findViewById(R.id.quizNameTV);
                quizMoreDetailsArrowIV = itemView.findViewById(R.id.quizMoreDetailsArrowIV);

            case AppConstants.SECTION_QUIZ_TRENDING:
                quizTrendingRV = itemView.findViewById(R.id.quizTrendingRV);
                quizTrendingRL = itemView.findViewById(R.id.quizTrendingRL);
                quizTrendingNameTV = itemView.findViewById(R.id.quizTrendingNameTV);

            case AppConstants.SECTION_QUIZ_CATEGORY:
                quizCategoryRV = itemView.findViewById(R.id.quizCategoryRV);
                quizCategoryHeaderTV = itemView.findViewById(R.id.quizCategoryheaderTV);
                quizCategoryRL = itemView.findViewById(R.id.quizCategoryRL);

            case AppConstants.SECTION_QUIZ_LATEST:
                quizLatestRV = itemView.findViewById(R.id.quizLatestRV);
                quizLatestTV = itemView.findViewById(R.id.quizLatestTV);
                quizLatestRL = itemView.findViewById(R.id.quizLatestRL);
        }
    }
}