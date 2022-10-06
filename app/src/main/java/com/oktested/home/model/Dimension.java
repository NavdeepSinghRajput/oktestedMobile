package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Dimension implements Parcelable {

    public int width;
    public int height;

    protected Dimension(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<Dimension> CREATOR = new Creator<Dimension>() {
        @Override
        public Dimension createFromParcel(Parcel in) {
            return new Dimension(in);
        }

        @Override
        public Dimension[] newArray(int size) {
            return new Dimension[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(width);
        parcel.writeInt(height);
    }
}