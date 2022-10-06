package com.oktested.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.FriendListResponse.FriendListModel;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizFriendAdapter extends RecyclerView.Adapter<QuizFriendAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FriendListModel> friendListModels;
    private Room room;

    public QuizFriendAdapter(Context context, ArrayList<FriendListModel> friendListModels, Room room) {
        this.context = context;
        this.friendListModels = friendListModels;
        this.room = room;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlist_quiz_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(friendListModels.get(position).display_name)) {
            holder.friendName.setText(friendListModels.get(position).display_name);
        }

        if (friendListModels.get(position).is_online) {
            if (friendListModels.get(position).is_busy) {
                holder.addFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_yellow_bgcolor));
                holder.addFriend.setTextColor(context.getResources().getColor(R.color.colorblack));
                holder.addFriend.setText("Busy");
                holder.addFriend.setTextSize(10);
            } else {
                holder.addFriend.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.addFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_pink_bgcolor));
                holder.addFriend.setText("+");
                holder.addFriend.setTextSize(12);
            }
            holder.userOnline.setVisibility(View.VISIBLE);

        } else {

            holder.userOnline.setVisibility(View.GONE);
            holder.addFriend.setBackground(context.getResources().getDrawable(R.drawable.invite_round_bg));
            holder.addFriend.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.addFriend.setText("offline");
            holder.addFriend.setTextSize(10);

        }
        if (Helper.isContainValue(friendListModels.get(position).picture)) {
            Glide.with(context)
                    .load(friendListModels.get(position).picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(holder.friendPic);
        } else {
            holder.friendPic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.addFriend.getText().toString().trim().equalsIgnoreCase("+")) {
                    if (friendListModels.size() != 0) {
                        room.createRoom(friendListModels.get(position).id);
                    } else {
                        Toast.makeText(context, "sadas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (friendListModels.size() < 10) {
            return friendListModels.size();
        } else {
            return 10;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void notifyItem(ArrayList<FriendListModel> dataItemList) {
        this.friendListModels.addAll(dataItemList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        friendListModels.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, addFriend;
        CircleImageView friendPic;
        ImageView userOnline;

        ViewHolder(View v) {
            super(v);
            friendPic = v.findViewById(R.id.friendPic);
            friendName = v.findViewById(R.id.friendName);
            addFriend = v.findViewById(R.id.addFriend);
            userOnline = v.findViewById(R.id.userOnline);
        }
    }

    public interface Room {
        void createRoom(String friendId);
    }
}