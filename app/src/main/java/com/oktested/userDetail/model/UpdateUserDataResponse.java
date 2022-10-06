package com.oktested.userDetail.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class UpdateUserDataResponse extends BaseResponse {

    @SerializedName("email")
    public String email;
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("picture")
    public String picture;
}