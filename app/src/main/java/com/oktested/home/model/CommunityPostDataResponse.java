package com.oktested.home.model;

import com.oktested.core.model.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class CommunityPostDataResponse extends BaseResponse implements Serializable {

    public String id;
    public String type;
    public String link;
    public String description;
    public String created;
    public String community_id;
    public String community_name;
    public String community_slug;
    public String media;
    public String user_name;
    public String user_picture;
    public String thumbnail_image;
    public String is_reacted;
    public int total_reactions;
    public String community_image;
    public String duration;
    public String title;
    public String subtitle;
    public int comment_count;
    public int share_count;
    public Dimension dimension;
    public ArrayList<Album> album;
    public ArrayList<Choices> choices;
    public boolean is_polled;
    public String selected_poll_option;
    public long playbackPosition = 0;
    public boolean playerAlreadyAdded = false;

    public class Dimension implements Serializable {
        public int width;
        public int height;
    }

    public class Album implements Serializable {
        public String url;
    }

    public String getIs_reacted() {
        return is_reacted;
    }

    public void setIs_reacted(String is_reacted) {
        this.is_reacted = is_reacted;
    }

    public int getTotal_reactions() {
        return total_reactions;
    }

    public void setTotal_reactions(int total_reactions) {
        this.total_reactions = total_reactions;
    }

    public int getShare_count() {
        return share_count;
    }

    public void setShare_count(int share_count) {
        this.share_count = share_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public void setPlaybackPosition(long playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public boolean isPlayerAlreadyAdded() {
        return playerAlreadyAdded;
    }

    public void setPlayerAlreadyAdded(boolean playerAlreadyAdded) {
        this.playerAlreadyAdded = playerAlreadyAdded;
    }
}