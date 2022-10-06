package com.oktested.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CastCrewItem implements Parcelable {

    @SerializedName("displayname")
    public String displayname;

    @SerializedName("title")
    public String title;

    @SerializedName("username")
    public String username;

    @SerializedName("profile_pic")
    public String profile_pic;

    protected CastCrewItem(Parcel in) {
        displayname = in.readString();
        title = in.readString();
        username = in.readString();
        profile_pic = in.readString();
    }

    public static final Creator<CastCrewItem> CREATOR = new Creator<CastCrewItem>() {
        @Override
        public CastCrewItem createFromParcel(Parcel in) {
            return new CastCrewItem(in);
        }

        @Override
        public CastCrewItem[] newArray(int size) {
            return new CastCrewItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(displayname);
        parcel.writeString(title);
        parcel.writeString(username);
        parcel.writeString(profile_pic);
    }
}