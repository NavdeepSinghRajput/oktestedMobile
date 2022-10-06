package com.oktested.videoPlayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;

import java.util.ArrayList;

public class ReportIssueAdapter extends RecyclerView.Adapter<ReportIssueAdapter.ViewHolder> {

    private Context context;
    private ReportIssue reportIssue;
    private ArrayList<String> videoIssuesAL;

    public ReportIssueAdapter(Context context, ArrayList<String> videoIssuesAL, ReportIssue reportIssue) {
        this.context = context;
        this.reportIssue = reportIssue;
        this.videoIssuesAL = videoIssuesAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_issue_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.reportIssueTV.setText(videoIssuesAL.get(position));
        holder.reportIssueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportIssue.reportVideoIssue(videoIssuesAL.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoIssuesAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportIssueTV;

        ViewHolder(View itemView) {
            super(itemView);
            reportIssueTV = itemView.findViewById(R.id.reportIssueTV);
        }
    }

    public interface ReportIssue {
        void reportVideoIssue(String videoIssue);
    }
}