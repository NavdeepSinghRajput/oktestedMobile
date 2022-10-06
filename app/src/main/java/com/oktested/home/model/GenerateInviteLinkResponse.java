package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class GenerateInviteLinkResponse extends BaseResponse {

    @SerializedName("link")
    public String link;
}
