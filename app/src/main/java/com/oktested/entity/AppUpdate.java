package com.oktested.entity;

import com.google.gson.annotations.SerializedName;

public class AppUpdate {

    @SerializedName("version_name")
    public String version_name;

    @SerializedName("force_update")
    public boolean force_update;

    @SerializedName("message")
    public String message;

    @SerializedName("title")
    public String title;

    @SerializedName("version_code")
    public long version_code;
}