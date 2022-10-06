package com.oktested.videoPlayer.model;

import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class ReactionResponse extends BaseResponse {

    public int count;
    public String video_id;
    public ArrayList<ReactionCount> likes;
}