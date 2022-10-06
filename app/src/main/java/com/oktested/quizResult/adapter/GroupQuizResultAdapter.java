package com.oktested.quizResult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupQuizResultAdapter extends RecyclerView.Adapter<GroupQuizResultAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL;
    private int maxScore;

    public GroupQuizResultAdapter(Context context, ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL, int maxScore) {
        this.context = context;
        this.dataAL = dataAL;
        this.maxScore = maxScore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_result_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(dataAL.get(position).user_pic)) {
            Glide.with(context)
                    .load(dataAL.get(position).user_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.userIV);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(position).rank))) {
            holder.rankTV.setText(dataAL.get(position).rank + "");

            if (dataAL.get(position).rank == 1) {
                holder.winnerTV.setVisibility(View.VISIBLE);
            } else {
                holder.winnerTV.setVisibility(View.GONE);
            }
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(position).user_name))) {
            holder.nameTV.setText(dataAL.get(position).user_name);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(position).avg_time))) {
            holder.timeTV.setText(dataAL.get(position).avg_time + " sec/que");
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(position).total_score))) {
            holder.scoreTV.setText(dataAL.get(position).total_score + "/" + maxScore);
            holder.ratingBar.setNumStars(maxScore);
            holder.ratingBar.setMax(maxScore);
            holder.ratingBar.setRating(dataAL.get(position).total_score);
        }
    }

    @Override
    public int getItemCount() {
        return dataAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIV;
        TextView rankTV, nameTV, scoreTV, timeTV, winnerTV;
        RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);
            userIV = itemView.findViewById(R.id.userIV);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rankTV = itemView.findViewById(R.id.rankTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            scoreTV = itemView.findViewById(R.id.scoreTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            winnerTV = itemView.findViewById(R.id.winnerTV);
        }
    }
}