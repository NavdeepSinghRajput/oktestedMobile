package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class HomeStructureResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<HomeStructureListModel> data;
}