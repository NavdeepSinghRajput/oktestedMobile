package com.oktested.login.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class CreateUserDataResponse extends BaseResponse {

    @SerializedName("access_token")
    public String access_token;

    @SerializedName("email")
    public String email;

    @SerializedName("name")
    public String name;

    @SerializedName("display_name")
    public String display_name;

    @SerializedName("id")
    public String id;

    @SerializedName("gender")
    public String gender;

    @SerializedName("dob")
    public String dob;


    public CreateUserDataResponse() {
    }
}