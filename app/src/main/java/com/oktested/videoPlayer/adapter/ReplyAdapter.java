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
import com.oktested.videoPlayer.model.CommentReplyDataResponse;

import java.util.ArrayList;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommentReplyDataResponse> replyDataResponses;
    private ReplyDelete replyDelete;
    private ReplyReaction replyReaction;
    private ReplyComment replyComment;
    private ReplyReport replyReport;

    public ReplyAdapter(Context context, ArrayList<CommentReplyDataResponse> replyDataResponses, ReplyDelete replyDelete, ReplyReaction replyReaction, ReplyComment replyComment, ReplyReport replyReport) {
        this.context = context;
        this.replyDataResponses = replyDataResponses;
        this.replyDelete = replyDelete;
        this.replyReaction = replyReaction;
        this.replyComment = replyComment;
        this.replyReport = replyReport;
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
        if (Helper.isContainValue(replyDataResponses.get(position).user.picture)) {
            Glide.with(context)
                    .load(replyDataResponses.get(position).user.picture)
                    .centerCrop()
                    .placeholder(R.drawable.ic_vector_default_profile)
                    .into(holder.userIV);
        } else {
            holder.userIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_default_profile));
        }

        // For the user name on comment
        if (Helper.isContainValue(replyDataResponses.get(position).user.display_name)) {
            holder.userNameTV.setText(replyDataResponses.get(position).user.display_name);
        } else {
            if (Helper.isContainValue(replyDataResponses.get(position).user.name)) {
                holder.userNameTV.setText(replyDataResponses.get(position).user.name);
            } else {
                if (Helper.isContainValue(replyDataResponses.get(position).userid)) {
                    holder.userNameTV.setText("user" + replyDataResponses.get(position).userid.substring(0, 10) + "...");
                }
            }
        }

        // For set the verified anchor badge
        if (Helper.isContainValue(String.valueOf(replyDataResponses.get(position).user.okt_verified)) && replyDataResponses.get(position).user.okt_verified == 1) {
            holder.badgeIV.setVisibility(View.VISIBLE);
        } else {
            holder.badgeIV.setVisibility(View.GONE);
        }

        // For set the user comment
        if (Helper.isContainValue(replyDataResponses.get(position).comment)) {
            if (Helper.isContainValue(replyDataResponses.get(position).reply_to)){
                holder.commentTV.setText("@" + replyDataResponses.get(position).reply_to + " " + replyDataResponses.get(position).comment);
            } else {
                holder.commentTV.setText(replyDataResponses.get(position).comment);
            }
        }

        // For set the comment reply count
        if (Helper.isContainValue(String.valueOf(replyDataResponses.get(position).total_replies))) {
            if (replyDataResponses.get(position).total_replies < 1) {
                holder.replyCountTV.setText("Reply");
            } else if (replyDataResponses.get(position).total_replies == 1) {
                holder.replyCountTV.setText("1 reply");
            } else if (replyDataResponses.get(position).total_replies > 1) {
                holder.replyCountTV.setText(replyDataResponses.get(position).total_replies + " replies");
            }
        }

        // For set the comment reaction
        if (replyDataResponses.get(position).is_reacted) {
            holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));
        } else {
            holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));
        }

        // For set the reaction count
        if (Helper.isContainValue(String.valueOf(replyDataResponses.get(position).total_reactions))) {
            holder.commentLikeTV.setText(replyDataResponses.get(position).total_reactions + "");
        }

        // Click listener of side menu option
        holder.sideMenuRL.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.sideMenuRL);
            popup.getMenuInflater().inflate(R.menu.comment_menu, popup.getMenu());

            MenuItem deleteItem = popup.getMenu().findItem(R.id.delete);
            MenuItem reportItem = popup.getMenu().findItem(R.id.report);

            if (replyDataResponses.get(position).is_admin) {
                SpannableString deleteString = new SpannableString("Delete");
                deleteString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, deleteString.length(), 0);
                deleteItem.setTitle(deleteString);

                SpannableString reportString = new SpannableString("Report");
                reportString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, reportString.length(), 0);
                reportItem.setTitle(reportString);
            } else {
                if (replyDataResponses.get(position).is_editable) {
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
                            replyDelete.replyDelete(replyDataResponses.get(position).id, position);
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Helper.isNetworkAvailable(context)) {
                            replyReport.reportReply(replyDataResponses.get(position).id, replyDataResponses.get(position).parent_id);
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
        holder.commentReactionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isNetworkAvailable(context)) {
                    if (replyDataResponses.get(position).is_reacted) {
                        replyDataResponses.get(position).setIs_reacted(false);
                        replyDataResponses.get(position).setTotal_reactions(replyDataResponses.get(position).total_reactions - 1);

                        holder.commentLikeTV.setText((replyDataResponses.get(position).total_reactions) + "");
                        holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_heart));

                        replyReaction.setReplyReaction("rm", replyDataResponses.get(position).id, replyDataResponses.get(position).parent_id);
                    } else {
                        replyDataResponses.get(position).setIs_reacted(true);
                        replyDataResponses.get(position).setTotal_reactions(replyDataResponses.get(position).total_reactions + 1);

                        holder.commentLikeTV.setText((replyDataResponses.get(position).total_reactions) + "");
                        holder.commentLikeIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vector_comment_like_heart));

                        replyReaction.setReplyReaction("up", replyDataResponses.get(position).id, replyDataResponses.get(position).parent_id);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.replyCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyComment.setReplyComment(replyDataResponses.get(position).userid, "@" + holder.userNameTV.getText().toString().trim());
            }
        });
    }

    @Override
    public int getItemCount() {
        return replyDataResponses.size();
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

    public void setReplySectionDataOnScroll(ArrayList<CommentReplyDataResponse> replyDataResponses) {
        this.replyDataResponses.addAll(replyDataResponses);
        notifyDataSetChanged();
    }

    public void refreshReplyAfterDelete(int replyPosition) {
        replyDataResponses.remove(replyPosition);
        notifyDataSetChanged();
    }

    public interface ReplyDelete {
        void replyDelete(String replyId, int position);
    }

    public interface ReplyReaction {
        void setReplyReaction(String type, String replyId, String parent_id);
    }

    public interface ReplyComment {
        void setReplyComment(String userid, String userName);
    }

    public interface ReplyReport {
        void reportReply(String replyId, String parentId);
    }
}