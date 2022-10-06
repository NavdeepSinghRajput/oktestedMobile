package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;

public class HomeStructureListModel {

    @SerializedName("section_type")
    public String section_type;

    @SerializedName("value")
    public HomeSectionValue value;

    public class HomeSectionValue {

        @SerializedName("slug")
        public String slug;

        @SerializedName("section_title")
        public String section_title;

        @SerializedName("image_path")
        public String image_path;

        @SerializedName("image_link")
        public String image_link;

        @SerializedName("ad_code")
        public String ad_code;

        @SerializedName("is_mid_ad")
        public boolean is_mid_ad;
    }
}