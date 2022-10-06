package com.oktested.entity;

import com.google.gson.annotations.SerializedName;

public class AdSettingDataResponse {

    @SerializedName("ad_type")
    public String ad_type;

    @SerializedName("value")
    public AdSettingValue value;

    public class AdSettingValue {

        @SerializedName("status")
        public boolean status;

        @SerializedName("ad_code")
        public String ad_code;

        @SerializedName("is_mid_ad")
        public boolean is_mid_ad;

        @SerializedName("count")
        public int count;
    }
}