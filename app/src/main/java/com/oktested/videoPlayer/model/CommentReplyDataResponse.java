package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class CommentReplyDataResponse extends BaseResponse {

    @SerializedName("comment")
    public String comment;
    @SerializedName("post_id")
    public String post_id;
    @SerializedName("slug")
    public String slug;
    @SerializedName("userid")
    public String userid;
    @SerializedName("user")
    public User user;
    @SerializedName("id")
    public String id;
    @SerializedName("parent_id")
    public String parent_id;
    @SerializedName("total_replies")
    public int total_replies;
    @SerializedName("total_reactions")
    public int total_reactions;
    @SerializedName("is_editable")
    public boolean is_editable;
    @SerializedName("is_reacted")
    public boolean is_reacted;
    @SerializedName("reply_to")
    public String reply_to;
    @SerializedName("reply_to_id")
    public String reply_to_id;
    @SerializedName("is_admin")
    public boolean is_admin;

    public int getTotal_reactions() {
        return total_reactions;
    }

    public void setTotal_reactions(int total_reactions) {
        this.total_reactions = total_reactions;
    }

    public boolean isIs_reacted() {
        return is_reacted;
    }

    public void setIs_reacted(boolean is_reacted) {
        this.is_reacted = is_reacted;
    }
}