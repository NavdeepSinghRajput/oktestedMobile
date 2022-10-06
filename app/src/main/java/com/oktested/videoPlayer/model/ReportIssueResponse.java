package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class ReportIssueResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<String> videoIssues;
}