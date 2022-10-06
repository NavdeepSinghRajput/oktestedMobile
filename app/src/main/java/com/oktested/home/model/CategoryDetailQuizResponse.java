package com.oktested.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailQuizResponse extends BaseResponse {

    @SerializedName("cat_data")
    @Expose
    public CategoryDetail cat_data;

    @SerializedName("data")
    @Expose
    public ArrayList<QuizListModel> data;

    public class CategoryDetail {

        @SerializedName("category_feature_img")
        @Expose
        public String category_feature_img;

        @SerializedName("category_slug")
        @Expose
        public String category_slug;

        @SerializedName("category_display")
        @Expose
        public String category_display;

        @SerializedName("category_desc")
        @Expose
        public String category_desc;

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("redis_id")
        @Expose
        public String redis_id;

        @SerializedName("onexone_img")
        @Expose
        public String onexone_img;
    }
}