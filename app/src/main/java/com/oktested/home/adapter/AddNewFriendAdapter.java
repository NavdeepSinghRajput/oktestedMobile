package com.oktested.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.ui.FriendsActionInterface;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.Helper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewFriendAdapter extends RecyclerView.Adapter<AddNewFriendAdapter.ViewHolder> {

    private Context context;
    private List<ContactModel> userContactList;
    private FriendsActionInterface friendsActionInterface;

    public AddNewFriendAdapter(Context context, FriendsActionInterface friendsActionInterface, List<ContactModel> userContactList) {
        this.context = context;
        this.userContactList = userContactList;
        this.friendsActionInterface = friendsActionInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_friendlist_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(userContactList.get(position).name)) {
            holder.friendName.setText(userContactList.get(position).name);
        }
        if (Helper.isContainValue(userContactList.get(position).status)) {
            if (userContactList.get(position).status.equalsIgnoreCase("pending")) {
                holder.addNewFriend.setText("Pending");
                holder.addNewFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_yellow_bgcolor));
            } else if (userContactList.get(position).status.equalsIgnoreCase("invite")) {
                holder.addNewFriend.setText("Invite");
                holder.addNewFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_greyish_bgcolor));
            }else if (userContactList.get(position).status.equalsIgnoreCase("pendingfriendRequest")) {
                holder.addNewFriend.setText("Accept");
                holder.addNewFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_yellow_bgcolor));
            } else {
                holder.addNewFriend.setText("Add");
                holder.addNewFriend.setBackground(context.getResources().getDrawable(R.drawable.rounded_yellow_bgcolor));
            }
        }
        if (Helper.isContainValue(userContactList.get(position).picture)) {
            Glide.with(context)
                    .load(userContactList.get(position).picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(holder.friendPic);
        } else {
            holder.friendPic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }
        holder.addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.postDelayThreeSecond(holder.addNewFriend);
                if (userContactList.get(position).status.equalsIgnoreCase("pending")) {
                    holder.addNewFriend.setText("Pending");
                } else if (userContactList.get(position).status.equalsIgnoreCase("invite")) {
                    friendsActionInterface.callInviteApi(userContactList.get(position).contact_no);
                } else if (userContactList.get(position).status.equalsIgnoreCase("pendingfriendRequest")) {
                    friendsActionInterface.callFriendAcceptApi(userContactList.get(position),position);
                } else {
                    friendsActionInterface.callAddRequestApi(position, userContactList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userContactList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void clearALL() {
        userContactList.clear();
        notifyDataSetChanged();
    }

    public void notifyRemoved(int position) {
        userContactList.remove(position);
        notifyDataSetChanged();
    }

    public void clearAll() {
        userContactList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView addNewFriend;
        CircleImageView friendPic;

        ViewHolder(View v) {
            super(v);
            friendPic = v.findViewById(R.id.friendPic);
            friendName = v.findViewById(R.id.friendName);
            addNewFriend = v.findViewById(R.id.addNewFriend);
        }
    }


    public void notifyItem(List<ContactModel> dataItemList) {
        this.userContactList.clear();
        this.userContactList.addAll(dataItemList);
        notifyDataSetChanged();
    }

    public void notifyItemPostion(int postion, ContactModel dataItemList) {
        this.userContactList.set(postion, dataItemList);
        notifyDataSetChanged();
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
        });
    }

}