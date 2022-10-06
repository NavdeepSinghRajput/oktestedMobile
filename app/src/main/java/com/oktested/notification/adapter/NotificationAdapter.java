package com.oktested.notification.adapter;

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
import com.oktested.notification.model.NotifcationModelItem;
import com.oktested.notification.ui.AcceptRejectRequestInterface;
import com.oktested.utils.Helper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NotifcationModelItem> notifcationModels;
    private AcceptRejectRequestInterface acceptRejectRequestInterface;

    public NotificationAdapter(Context context, AcceptRejectRequestInterface acceptRejectRequestInterface, ArrayList<NotifcationModelItem> notifcationModels) {
        this.context = context;
        this.notifcationModels = notifcationModels;
        this.acceptRejectRequestInterface = acceptRejectRequestInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(notifcationModels.get(position).data_message.message_body)) {
            holder.notificationTitle.setText(notifcationModels.get(position).data_message.message_body);
        }
        if (Helper.isContainValue(notifcationModels.get(position).type)) {
            if (notifcationModels.get(position).type.equalsIgnoreCase("friend_request") && notifcationModels.get(position).action_type.length() == 0) {
                holder.actionLL.setVisibility(View.VISIBLE);
            } else {
                holder.actionLL.setVisibility(View.GONE);
            }

        } else {
            holder.actionLL.setVisibility(View.GONE);
        }
        holder.acceptFriendRqt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRejectRequestInterface.callAcceptRequestApi(notifcationModels.get(position).data_message, position);
            }
        });
        String time = setPostDate(notifcationModels.get(position).send_at);
        if (time.length() > 0)
            holder.notificationTime.setText(time);
        holder.rejectFriendRqt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRejectRequestInterface.callRejectRequestApi(notifcationModels.get(position).data_message, position);
            }
        });
        if (Helper.isContainValue(notifcationModels.get(position).data_message.image)) {
            Glide.with(context)
                    .load(notifcationModels.get(position).data_message.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(holder.notificationPic);
        } else {
            holder.notificationPic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }
    }

    private String setPostDate(String createdDate) {
        try {
            if (createdDate.contains("T")) {
                createdDate = createdDate.replace("T", " ");
            }
            long postTime = Helper.getTimeInMillisecondsFromDate(createdDate);
            long currentTime = System.currentTimeMillis();
            long diffInDays = Helper.getDateDiff(postTime, currentTime, TimeUnit.DAYS);
            if (diffInDays == 1) {
                return "1 day ago";
            } else if (diffInDays > 1) {
                return diffInDays + " days ago";
            } else if (diffInDays == 0) {
                long diffInHour = Helper.getDateDiff(postTime, currentTime, TimeUnit.HOURS);
                if (diffInHour == 1) {
                    return "1 hour ago";
                } else if (diffInHour > 1) {
                    return diffInHour + " hours ago";
                } else if (diffInHour == 0) {
                    long diffInMinute = Helper.getDateDiff(postTime, currentTime, TimeUnit.MINUTES);
                    if (diffInMinute > 1) {
                        return diffInMinute + " minutes ago";
                    } else {
                        return "1 minute ago";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return notifcationModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void clearAll() {
        notifcationModels.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, acceptFriendRqt, rejectFriendRqt, notificationTime;
        CircleImageView notificationPic;
        LinearLayout actionLL;

        ViewHolder(View v) {
            super(v);
            notificationTitle = v.findViewById(R.id.notificationTitle);
            acceptFriendRqt = v.findViewById(R.id.acceptFriendRqt);
            rejectFriendRqt = v.findViewById(R.id.rejectFriendRqt);
            notificationTime = v.findViewById(R.id.notificationTime);
            notificationPic = v.findViewById(R.id.notificationPic);
            actionLL = v.findViewById(R.id.actionLL);
        }
    }

    public void notifyItem(ArrayList<NotifcationModelItem> dataItemList) {
        this.notifcationModels.addAll(dataItemList);
        notifyDataSetChanged();
    }

    public void notifyItemPostion(int postion, NotifcationModelItem dataItemList) {
        this.notifcationModels.set(postion, dataItemList);
        notifyDataSetChanged();
    }

    public void removeNotify(int position) {
        notifcationModels.remove(position);
        //   notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
        });
    }
}