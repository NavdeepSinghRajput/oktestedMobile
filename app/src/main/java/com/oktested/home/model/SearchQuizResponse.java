package com.oktested.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class SearchQuizResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    public ArrayList<QuizListModel> data;

    @SerializedName("next_page")
    @Expose
    public String next_page;
}
