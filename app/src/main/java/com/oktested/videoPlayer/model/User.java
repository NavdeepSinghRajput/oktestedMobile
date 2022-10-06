package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("display_name")
    public String display_name;
    @SerializedName("picture")
    public String picture;
    @SerializedName("okt_verified")
    public int okt_verified;
}