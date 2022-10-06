package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AnchorsListModel implements Serializable {
    public String username;
    public String profile_pic;
    public String first_name;
    public String last_name;
    public String display_name;
    public String updated_by;
    public int exists;
    public String modified_date;
    public String cover_photo;
    public String about_me;
    public String _id;
    public String email;
    @SerializedName("social_links")
    public List<SocialLinks> social_links;

    public class SocialLinks implements Serializable {
        public String link;
        public String network;
    }
}