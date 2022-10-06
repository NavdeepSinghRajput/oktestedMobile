package com.oktested.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class LatestQuizResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
   public ArrayList<QuizListModel> data;
}
