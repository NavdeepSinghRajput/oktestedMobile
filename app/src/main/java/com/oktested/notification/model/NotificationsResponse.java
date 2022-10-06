package com.oktested.notification.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class NotificationsResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<NotifcationModelItem> data;

    @SerializedName("next_page")
    public int next_page;

}
