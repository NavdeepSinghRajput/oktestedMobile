package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class CommentReplyResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<CommentReplyDataResponse> data;
    @SerializedName("next_page")
    public int next_page;
    @SerializedName("total_replies")
    public int total_replies;
}