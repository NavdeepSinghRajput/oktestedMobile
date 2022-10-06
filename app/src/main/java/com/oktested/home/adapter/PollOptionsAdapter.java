package com.oktested.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.home.model.CommunityPostDataResponse;
import com.oktested.utils.Helper;

public class PollOptionsAdapter extends RecyclerView.Adapter<PollOptionsAdapter.ViewHolder> {

    private Context context;
    private CommunityPostDataResponse postDataResponse;
    private boolean callClickListener = true;
    private Poll poll;

    public PollOptionsAdapter(Context context, CommunityPostDataResponse postDataResponse, Poll poll) {
        this.context = context;
        this.postDataResponse = postDataResponse;
        this.poll = poll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_option_item, parent, false);
        return new PollOptionsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(postDataResponse.choices.get(position).name)) {
            holder.optionTV.setText(postDataResponse.choices.get(position).name);
        }

        if (postDataResponse.is_polled) {
            holder.resultTV.setVisibility(View.VISIBLE);
            if (Helper.isContainValue(String.valueOf(postDataResponse.choices.get(position).res))) {
                holder.resultTV.setText(postDataResponse.choices.get(position).res + "%");
                holder.progressBar.setProgress(postDataResponse.choices.get(position).res);
            }

            if (postDataResponse.choices.get(position).name.equalsIgnoreCase(postDataResponse.selected_poll_option)) {
                holder.resultTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.parentRL.setBackground(context.getResources().getDrawable(R.drawable.poll_option_select));
            }
        }

        holder.parentRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!postDataResponse.is_polled && callClickListener) {
                    if (Helper.isNetworkAvailable(context)) {
                        callClickListener = false;
                        holder.resultTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        holder.parentRL.setBackground(context.getResources().getDrawable(R.drawable.poll_option_select));
                        poll.submitPoll(postDataResponse.choices.get(position).name);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postDataResponse.choices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView optionTV, resultTV;
        public RelativeLayout parentRL;
        public ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            optionTV = itemView.findViewById(R.id.optionTV);
            resultTV = itemView.findViewById(R.id.resultTV);
            parentRL = itemView.findViewById(R.id.parentRL);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public interface Poll {
        void submitPoll(String selectedOption);
    }

    public void setPollResult(CommunityPostDataResponse postDataResponse) {
        this.postDataResponse = postDataResponse;
        notifyDataSetChanged();
    }
}