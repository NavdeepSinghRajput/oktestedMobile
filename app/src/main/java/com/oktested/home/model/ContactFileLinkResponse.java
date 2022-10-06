package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class ContactFileLinkResponse extends BaseResponse {

    @SerializedName("url")
    public String url;
    @SerializedName("filename")
    public String filename;
}
