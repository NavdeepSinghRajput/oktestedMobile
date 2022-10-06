package com.oktested.videoCall.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;
import com.oktested.home.model.QuizListModel;

import java.util.ArrayList;

public class RecommendedQuizResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    public ArrayList<QuizListModel> data;
}