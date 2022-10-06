package com.oktested.videoPlayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.entity.CastCrewItem;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class CastCrewAdapter extends RecyclerView.Adapter<CastCrewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CastCrewItem> castCrew;
    private GetUserDataresponse userDataResponse;
    private FollowActor followActor;

    public CastCrewAdapter(Context context, ArrayList<CastCrewItem> castCrew, GetUserDataresponse userDataResponse, FollowActor followActor) {
        this.context = context;
        this.castCrew = castCrew;
        this.userDataResponse = userDataResponse;
        this.followActor = followActor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_crew_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(castCrew.get(position).displayname)) {
            holder.actorNameTV.setText(castCrew.get(position).displayname);
        }
        if (Helper.isContainValue(castCrew.get(position).profile_pic)) {
            Glide.with(context)
                    .load(castCrew.get(position).profile_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.actorImageIV);
        } else {
            holder.actorImageIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        if (userDataResponse != null && userDataResponse.follow != null) {
            setFollowButton(holder, position);
        }

        holder.followTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.followTV.getText().equals("Follow")) {
                    followActor.postFollowActor(castCrew.get(position).username, "Follow");

                    holder.followTV.setText("Following");
                    holder.followTV.setBackground(context.getResources().getDrawable(R.drawable.button_bg));
                    holder.followTV.setTextColor(Color.parseColor("#464646"));
                } else {
                    followActor.postFollowActor(castCrew.get(position).username, "Following");

                    holder.followTV.setText("Follow");
                    holder.followTV.setBackground(context.getResources().getDrawable(R.drawable.follow_bg));
                    holder.followTV.setTextColor(Color.parseColor("#717171"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return castCrew.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView actorImageIV;
        TextView actorNameTV, followTV;

        ViewHolder(View itemView) {
            super(itemView);
            actorImageIV = itemView.findViewById(R.id.actorImageIV);
            actorNameTV = itemView.findViewById(R.id.actorNameTV);
            followTV = itemView.findViewById(R.id.followTV);
        }
    }

    private void setFollowButton(ViewHolder holder, int position) {
        for (int i = 0; i < userDataResponse.follow.size(); i++) {
            if (userDataResponse.follow.get(i).equalsIgnoreCase(castCrew.get(position).username)) {
                holder.followTV.setBackground(context.getResources().getDrawable(R.drawable.button_bg));
                holder.followTV.setText("Following");
                holder.followTV.setTextColor(Color.parseColor("#464646"));
            }
        }
    }

    public interface FollowActor {
        void postFollowActor(String userName, String type);
    }
}