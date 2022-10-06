package com.oktested.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> searchHistoryAL;
    private SearchHistoryData searchHistoryData;
    private UserSearch userSearch;

    public SearchHistoryAdapter(Context context, ArrayList<String> searchHistoryAL, SearchHistoryData searchHistoryData, UserSearch userSearch) {
        this.context = context;
        this.searchHistoryAL = searchHistoryAL;
        this.searchHistoryData = searchHistoryData;
        this.userSearch = userSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (Helper.isContainValue(searchHistoryAL.get(position))) {
            holder.titleTV.setText(searchHistoryAL.get(position));
        }
        holder.copyIV.setOnClickListener(view -> searchHistoryData.setSearchHistory(searchHistoryAL.get(position)));

        holder.titleTV.setOnClickListener(view -> userSearch.callUserSearchFromAdapter(searchHistoryAL.get(position)));
    }

    @Override
    public int getItemCount() {
        return searchHistoryAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        ImageView copyIV;

        ViewHolder(View v) {
            super(v);
            copyIV = v.findViewById(R.id.copyIV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    public interface SearchHistoryData {
        void setSearchHistory(String searchHistory);
    }

    public interface UserSearch {
        void callUserSearchFromAdapter(String searchHistory);
    }
}