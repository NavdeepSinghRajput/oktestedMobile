package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;
import com.oktested.roomDatabase.model.ContactModel;

public class SendFriendRequestResponse  extends BaseResponse {

    @SerializedName("user_status")
   public String user_status;
    @SerializedName("data")
   public ContactModel data;
}
