package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class VideoCommentResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<VideoCommentDataResponse> data;
    @SerializedName("next_page")
    public int next_page;
    @SerializedName("total_comments")
    public int total_coments;
}