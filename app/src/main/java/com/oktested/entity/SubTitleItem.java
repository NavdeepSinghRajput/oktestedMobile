package com.oktested.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubTitleItem implements Parcelable {

    @SerializedName("language_name")
    public String language_name;

    @SerializedName("srt_url")
    public String srtfile;

    @SerializedName("language")
    public String language;

    protected SubTitleItem(Parcel in) {
        language_name = in.readString();
        srtfile = in.readString();
        language = in.readString();
    }

    public static final Creator<SubTitleItem> CREATOR = new Creator<SubTitleItem>() {
        @Override
        public SubTitleItem createFromParcel(Parcel in) {
            return new SubTitleItem(in);
        }

        @Override
        public SubTitleItem[] newArray(int size) {
            return new SubTitleItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(language_name);
        parcel.writeString(srtfile);
        parcel.writeString(language);
    }
}