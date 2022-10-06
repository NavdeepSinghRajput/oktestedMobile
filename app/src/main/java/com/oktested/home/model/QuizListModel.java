package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuizListModel implements Parcelable {

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("slug")
    @Expose
    public String slug;

    @SerializedName("feature_img_1x1")
    @Expose
    public String feature_img_1x1;

    @SerializedName("article_id")
    @Expose
    public String article_id;

    @SerializedName("cat_display")
    @Expose
    public ArrayList<CatDisplay> cat_display = new ArrayList<>();

    @SerializedName("post_json_content")
    @Expose
    public PostJsonContent post_json_content;

    public static final Creator<QuizListModel> CREATOR = new Creator<QuizListModel>() {
        @Override
        public QuizListModel createFromParcel(Parcel in) {
            return new QuizListModel(in);
        }

        @Override
        public QuizListModel[] newArray(int size) {
            return new QuizListModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(slug);
        dest.writeString(feature_img_1x1);
        dest.writeString(article_id);
        dest.writeTypedList(cat_display);
    }

    protected QuizListModel(Parcel in) {
        title = in.readString();
        slug = in.readString();
        feature_img_1x1 = in.readString();
        article_id = in.readString();
        in.readTypedList(cat_display, CatDisplay.CREATOR);
    }

    public class PostJsonContent {
        @SerializedName("questions")
        @Expose
        public ArrayList<Questions> questions;

        @SerializedName("ques_length")
        @Expose
        public int ques_length;
    }
}