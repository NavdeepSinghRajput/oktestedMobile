package com.oktested.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdSettingResponse {

    @SerializedName("enable")
    public boolean enable;

    @SerializedName("data")
    public ArrayList<AdSettingDataResponse> data;
}