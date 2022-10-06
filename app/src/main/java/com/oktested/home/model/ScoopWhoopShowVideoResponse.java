package com.oktested.home.model;

import com.oktested.core.model.BaseResponse;
import com.oktested.entity.DataItem;
import com.oktested.entity.ShowDetails;

import java.util.ArrayList;

public class ScoopWhoopShowVideoResponse extends BaseResponse {
    public ArrayList<DataItem> data;

    public ShowDetails show_details;
}