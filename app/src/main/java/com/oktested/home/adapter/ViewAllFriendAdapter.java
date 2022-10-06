package com.oktested.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.FriendListResponse.FriendListModel;
import com.oktested.utils.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAllFriendAdapter extends RecyclerView.Adapter<ViewAllFriendAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FriendListModel> friendListModels;
    private Room room;
    private boolean removeVisible;

    public ViewAllFriendAdapter(Context context, ArrayList<FriendListModel> friendListModels, Room room,boolean removeVisible) {
        this.context = context;
        this.friendListModels = friendListModels;
        this.room = room;
        this.removeVisible = removeVisible;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_friendlist_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(friendListModels.get(position).display_name)) {
            holder.friendName.setText(friendListModels.get(position).display_name);
        }

        if (friendListModels.get(position).is_online) {
            if (friendListModels.get(position).is_busy) {
                holder.offlineFriend.setVisibility(View.GONE);
                holder.addFriend.setVisibility(View.GONE);
                holder.busyFriend.setVisibility(View.VISIBLE);
            } else {
                 holder.offlineFriend.setVisibility(View.GONE);
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.busyFriend.setVisibility(View.GONE);
            }
            holder.userOnline.setVisibility(View.VISIBLE);

        } else {

            holder.userOnline.setVisibility(View.GONE);
            holder.offlineFriend.setVisibility(View.VISIBLE);
            holder.addFriend.setVisibility(View.GONE);
            holder.busyFriend.setVisibility(View.GONE);

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

        if(removeVisible){
            holder.removeFriend.setVisibility(View.VISIBLE);
        }else{
            holder.removeFriend.setVisibility(View.GONE);
        }
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.addFriend.getText().toString().trim().equalsIgnoreCase("+")) {
                       room.createRoom(friendListModels.get(position).id);
                }
            }
        });
        holder.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOnRemove(friendListModels.get(position).display_name,friendListModels.get(position).id,friendListModels.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendListModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void removeFriendPosition(int position) {
        friendListModels.remove(position);
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, addFriend, offlineFriend,busyFriend;
        CircleImageView friendPic;
        ImageView userOnline, removeFriend;

        ViewHolder(View v) {
            super(v);
            friendPic = v.findViewById(R.id.friendPic);
            friendName = v.findViewById(R.id.friendName);
            addFriend = v.findViewById(R.id.addFriend);
            userOnline = v.findViewById(R.id.userOnline);
            offlineFriend = v.findViewById(R.id.offlineFriend);
            busyFriend = v.findViewById(R.id.busyFriend);
            removeFriend = v.findViewById(R.id.removeFriend);
        }
    }

    public void clearAll() {
        friendListModels.clear();
    }

    public void notifyItem(ArrayList<FriendListModel> dataItemList) {
        this.friendListModels.addAll(dataItemList);
        notifyDataSetChanged();
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
        });
    }

    public interface Room {
        void createRoom(String friendId);

        void removeFriend(String friendId,int position);
    }

    private void showDialogOnRemove(String name, String id, int size) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat));
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.leave_room_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView messageTV = dialogView.findViewById(R.id.messageTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);
        TextView cancelTV = dialogView.findViewById(R.id.cancelTV);

        messageTV.setText("Are you sure you want to remove \n"+ name +" from \n" +
                "your friends.");

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                room.removeFriend(id,size);
                alertDialog.dismiss();
            }
        });
    }

}