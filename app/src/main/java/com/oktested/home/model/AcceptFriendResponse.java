package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;
import com.oktested.notification.model.NotifcationModelItem;

public class AcceptFriendResponse extends BaseResponse {

    @SerializedName("data")
    public NotifcationModelItem data;

}
