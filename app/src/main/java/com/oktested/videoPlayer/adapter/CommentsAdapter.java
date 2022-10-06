package com.oktested.videoPlayer.adapter;

import android.content.Context;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.model.VideoCommentDataResponse;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<VideoCommentDataResponse> videoCommentData;
    private Reaction reaction;
    private CommentDelete commentDelete;
    private ReplySection replySection;
    private CommentReport commentReport;

    public CommentsAdapter(Context context, ArrayList<VideoCommentDataResponse> videoCommentData, Reaction reaction, CommentDelete commentDelete, ReplySection replySection, CommentReport commentReport) {
        this.context = context;
        this.videoCommentData = videoCommentData;
        this.commentDelete = commentDelete;
        this.commentReport = commentReport;
        this.reaction = reaction;
        this.replySection = replySection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // For set the user image
        if (Helper.isContainValue(videoCommentData.get(position).user.picture)) {
            Glide.with(context)
                    .load(videoCommentData.get(position).user.picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(holder.userIV);
        } else {
            holder.userIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }

        // For the user name on comment
        if (Helper.isContainValue(videoCommentData.get(position).user.display_name)) {
            holder.userNameTV.setText(videoCommentData.get(position).user.display_name);
        } else {
            if (Helper.isContainValue(videoCommentData.get(position).user.name)) {
                holder.userNameTV.setText(videoCommentData.get(position).user.name);
            } else {
                if (Helper.isContainValue(videoCommentData.get(position).userid)) {
                    holder.userNameTV.setText("user" + videoCommentData.get(position).userid.substring(0, 10) + "...");
                }
            }
        }

        // For set the verified anchor badge
        if (Helper.isContainValue(String.valueOf(videoCommentData.get(position).user.okt_verified)) && videoCommentData.get(position).user.okt_verified == 1) {
            holder.badgeIV.setVisibility(View.VISIBLE);
        } else {
            holder.badgeIV.setVisibility(View.GONE);
        }

        // For set the user comment
        if (Helper.isContainValue(videoCommentData.get(position).comment)) {
            holder.commentTV.setText(videoCommentData.get(position).comment);
        }

        // For set the comment reply count
        if (Helper.isContainValue(String.valueOf(videoCommentData.get(position).total_replies))) {
            if (videoCommentData.get(position).total_replies < 1) {
                holder.replyCountTV.setText("Reply");
            } else if (videoCommentData.get(position).total_replies == 1) {
                holder.replyCountTV.setText("1 reply");
            } else if (videoCommentData.get(position).total_replies > 1) {
                holder.replyCountTV.setText(videoCommentData.get(position).total_replies + " replies");
            }
        }

        // For set the comment reaction
        if (videoCommentData.get(position).is_reacted) {
            holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));
        } else {
            holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));
        }

        // For set the reaction count
        if (Helper.isContainValue(String.valueOf(videoCommentData.get(position).total_reactions))) {
            holder.commentLikeTV.setText(videoCommentData.get(position).total_reactions + "");
        }

        // Click listener of side menu option
        holder.sideMenuRL.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.sideMenuRL);
            popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());

            MenuItem deleteItem = popup.getMenu().findItem(R.id.delete);
            MenuItem reportItem = popup.getMenu().findItem(R.id.report);

            if (videoCommentData.get(position).is_admin) {
                SpannableString deleteString = new SpannableString("Delete");
                deleteString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, deleteString.length(), 0);
                deleteItem.setTitle(deleteString);

                SpannableString reportString = new SpannableString("Report");
                reportString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, reportString.length(), 0);
                reportItem.setTitle(reportString);
            } else {
                if (videoCommentData.get(position).is_editable) {
                    reportItem.setVisible(false);
                    SpannableString deleteString = new SpannableString("Delete");
                    deleteString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, deleteString.length(), 0);
                    deleteItem.setTitle(deleteString);
                } else {
                    deleteItem.setVisible(false);
                    SpannableString reportString = new SpannableString("Report");
                    reportString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, reportString.length(), 0);
                    reportItem.setTitle(reportString);
                }
            }

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                        if (Helper.isNetworkAvailable(context)) {
                            commentDelete.deleteComment(videoCommentData.get(position).id, position);
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            commentReport.reportComment(videoCommentData.get(position).id);
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
            });
            popup.show();
        });

        // Click listener of reaction
        holder.commentReactionLL.setOnClickListener(view -> {
            if (Helper.isNetworkAvailable(context)) {
                if (videoCommentData.get(position).is_reacted) {
                    videoCommentData.get(position).setIs_reacted(false);
                    videoCommentData.get(position).setTotal_reactions(videoCommentData.get(position).total_reactions - 1);

                    holder.commentLikeTV.setText((videoCommentData.get(position).total_reactions) + "");
                    holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));

                    reaction.setReaction("rm", videoCommentData.get(position).id);
                } else {
                    videoCommentData.get(position).setIs_reacted(true);
                    videoCommentData.get(position).setTotal_reactions(videoCommentData.get(position).total_reactions + 1);

                    holder.commentLikeTV.setText((videoCommentData.get(position).total_reactions) + "");
                    holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));

                    reaction.setReaction("up", videoCommentData.get(position).id);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        holder.replyCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replySection.setReplySection(videoCommentData.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoCommentData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userIV, commentLikeIV, badgeIV;
        RelativeLayout sideMenuRL;
        LinearLayout commentReactionLL;
        TextView userNameTV, commentTV, replyCountTV, commentLikeTV;

        ViewHolder(View itemView) {
            super(itemView);
            badgeIV = itemView.findViewById(R.id.badgeIV);
            userIV = itemView.findViewById(R.id.userIV);
            commentLikeIV = itemView.findViewById(R.id.commentLikeIV);
            sideMenuRL = itemView.findViewById(R.id.sideMenuRL);
            commentReactionLL = itemView.findViewById(R.id.commentReactionLL);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            commentTV = itemView.findViewById(R.id.commentTV);
            replyCountTV = itemView.findViewById(R.id.replyCountTV);
            commentLikeTV = itemView.findViewById(R.id.commentLikeTV);
        }
    }

    public void setCommentSectionData(ArrayList<VideoCommentDataResponse> videoCommentData) {
        this.videoCommentData = videoCommentData;
        notifyDataSetChanged();
    }

    public void setCommentSectionDataOnScroll(ArrayList<VideoCommentDataResponse> videoCommentData) {
        this.videoCommentData.addAll(videoCommentData);
        notifyDataSetChanged();
    }

    public void refreshCommentAfterDelete(int commentPosition) {
        if (videoCommentData != null && videoCommentData.size() > 0) {
            videoCommentData.remove(commentPosition);
            notifyDataSetChanged();
        }
    }

    public void refreshCommentAfterReactionFromReply() {
        notifyDataSetChanged();
    }

    public interface Reaction {
        void setReaction(String type, String commentId);
    }

    public interface CommentDelete {
        void deleteComment(String commentId, int position);
    }

    public interface CommentReport {
        void reportComment(String commentId);
    }

    public interface ReplySection {
        void setReplySection(VideoCommentDataResponse commentDataResponse, int position);
    }
}