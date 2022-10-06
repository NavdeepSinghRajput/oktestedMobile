package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class CategoryQuizResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    public ArrayList<CategoryQuizModel> data;

    public static class CategoryQuizModel implements Parcelable {

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

        public boolean selected = false;

        public CategoryQuizModel() {

        }

        protected CategoryQuizModel(Parcel in) {
            category_feature_img = in.readString();
            category_slug = in.readString();
            category_display = in.readString();
            category_desc = in.readString();
            id = in.readString();
            redis_id = in.readString();
            onexone_img = in.readString();
        }

        public static final Creator<CategoryQuizModel> CREATOR = new Creator<CategoryQuizModel>() {
            @Override
            public CategoryQuizModel createFromParcel(Parcel in) {
                return new CategoryQuizModel(in);
            }

            @Override
            public CategoryQuizModel[] newArray(int size) {
                return new CategoryQuizModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(category_feature_img);
            dest.writeString(category_slug);
            dest.writeString(category_display);
            dest.writeString(category_desc);
            dest.writeString(id);
            dest.writeString(redis_id);
            dest.writeString(onexone_img);
        }
    }
}