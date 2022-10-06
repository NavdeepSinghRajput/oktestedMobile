package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class FriendListResponse extends BaseResponse {

    public class FriendListModel {
        @SerializedName("id")
        public String id;
        @SerializedName("picture")
        public String picture;
        @SerializedName("display_name")
        public String display_name;
        @SerializedName("is_online")
        public boolean is_online;
        @SerializedName("is_busy")
        public boolean is_busy;

    }

    @SerializedName("type")
    public String type;

    @SerializedName("next_page")
    public int next_page;

    @SerializedName("data")
    public ArrayList<FriendListModel> data;
}