package com.oktested.quizDetail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;
import com.oktested.home.model.QuizListModel;

public class QuizDetailResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    public QuizListModel data;
}