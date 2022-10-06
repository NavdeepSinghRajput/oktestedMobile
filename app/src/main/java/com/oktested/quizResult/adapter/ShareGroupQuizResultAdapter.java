package com.oktested.quizResult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareGroupQuizResultAdapter extends RecyclerView.Adapter<ShareGroupQuizResultAdapter.ViewHolder> {

    private Context context;
    private int maxScore;
    private ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL;

    public ShareGroupQuizResultAdapter(Context context, ArrayList<GroupQuizResultResponse.GroupQuizResultDataResponse> dataAL, int maxScore) {
        this.context = context;
        this.dataAL = dataAL;
        this.maxScore = maxScore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_quiz_result_item, parent, false);
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
        }

        if (Helper.isContainValue(dataAL.get(position).user_name)) {
            holder.userNameTV.setText(dataAL.get(position).user_name);
        }

        if (Helper.isContainValue(String.valueOf(dataAL.get(position).total_score))) {
            holder.scoreTV.setText(dataAL.get(position).total_score + "/" + maxScore);
        }
    }

    @Override
    public int getItemCount() {
        return dataAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIV;
        TextView rankTV, userNameTV, scoreTV;

        ViewHolder(View itemView) {
            super(itemView);
            userIV = itemView.findViewById(R.id.userIV);
            rankTV = itemView.findViewById(R.id.rankTV);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            scoreTV = itemView.findViewById(R.id.scoreTV);
        }
    }
}