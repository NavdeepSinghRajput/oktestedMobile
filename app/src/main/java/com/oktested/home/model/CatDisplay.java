package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatDisplay implements Parcelable {

    @SerializedName("category_slug")
    @Expose
    public String category_slug;

    @SerializedName("category_display")
    @Expose
    public String category_display;

    protected CatDisplay(Parcel in) {
        category_slug = in.readString();
        category_display = in.readString();
    }

    public static final Creator<CatDisplay> CREATOR = new Creator<CatDisplay>() {
        @Override
        public CatDisplay createFromParcel(Parcel in) {
            return new CatDisplay(in);
        }

        @Override
        public CatDisplay[] newArray(int size) {
            return new CatDisplay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category_slug);
        parcel.writeString(category_display);
    }
}