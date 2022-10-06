package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionOption implements Parcelable {

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("value")
    @Expose
    public String value;

    @SerializedName("id")
    @Expose
    public String id;

    protected QuestionOption(Parcel in) {
        type = in.readString();
        value = in.readString();
        id = in.readString();
    }

    public static final Creator<QuestionOption> CREATOR = new Creator<QuestionOption>() {
        @Override
        public QuestionOption createFromParcel(Parcel in) {
            return new QuestionOption(in);
        }

        @Override
        public QuestionOption[] newArray(int size) {
            return new QuestionOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(value);
        parcel.writeString(id);
    }
}