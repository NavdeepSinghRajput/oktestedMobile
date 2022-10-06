package com.oktested.videoPlayer.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class SingleCommentResponse extends BaseResponse {

    @SerializedName("data")
    public VideoCommentDataResponse data;
}