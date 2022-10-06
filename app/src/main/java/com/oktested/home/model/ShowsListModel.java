package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShowsListModel implements Parcelable {

    @SerializedName("username")
    public String username;

    @SerializedName("profile_pic")
    public String profile_pic;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("display_name")
    public String display_name;

    @SerializedName("topic_slug")
    public String topic_slug;

    @SerializedName("topic_desc")
    public String topic_desc;

    @SerializedName("userID")
    public String userID;

    @SerializedName("updated_by")
    public String updated_by;

    @SerializedName("exists")
    public int exists;

    @SerializedName("flag")
    public int flag;

    @SerializedName("modified_date")
    public String modified_date;

    @SerializedName("cover_photo")
    public String cover_photo;

    @SerializedName("onexone_img")
    public String onexone_img;

    @SerializedName("feature_img_port")
    public String feature_img_port;

    @SerializedName("topic_name")
    public String topic_name;

    @SerializedName("about_me")
    public String about_me;

    @SerializedName("_id")
    public String _id;

    @SerializedName("email")
    public String email;

    @SerializedName("created_by")
    public String created_by;

    @SerializedName("user_registered")
    public String user_registered;

    @SerializedName("genre_slug")
    public String genre_slug;

    @SerializedName("genre_desc")
    public String genre_desc;

    @SerializedName("genre_title")
    public String genre_title;

    @SerializedName("redis_id")
    public String redis_id;

    @SerializedName("created_date")
    public String created_date;

    @SerializedName("sw_more")
    public boolean sw_more;

    protected ShowsListModel(Parcel in) {
        username = in.readString();
        profile_pic = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        display_name = in.readString();
        topic_slug = in.readString();
        topic_desc = in.readString();
        userID = in.readString();
        updated_by = in.readString();
        exists = in.readInt();
        flag = in.readInt();
        modified_date = in.readString();
        cover_photo = in.readString();
        onexone_img = in.readString();
        feature_img_port = in.readString();
        topic_name = in.readString();
        about_me = in.readString();
        _id = in.readString();
        email = in.readString();
        created_by = in.readString();
        user_registered = in.readString();
        genre_slug = in.readString();
        genre_desc = in.readString();
        genre_title = in.readString();
        redis_id = in.readString();
        created_date = in.readString();
        sw_more = in.readByte() != 0;
    }

    public static final Creator<ShowsListModel> CREATOR = new Creator<ShowsListModel>() {
        @Override
        public ShowsListModel createFromParcel(Parcel in) {
            return new ShowsListModel(in);
        }

        @Override
        public ShowsListModel[] newArray(int size) {
            return new ShowsListModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(profile_pic);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(display_name);
        parcel.writeString(topic_slug);
        parcel.writeString(topic_desc);
        parcel.writeString(userID);
        parcel.writeString(updated_by);
        parcel.writeInt(exists);
        parcel.writeInt(flag);
        parcel.writeString(modified_date);
        parcel.writeString(cover_photo);
        parcel.writeString(onexone_img);
        parcel.writeString(feature_img_port);
        parcel.writeString(topic_name);
        parcel.writeString(about_me);
        parcel.writeString(_id);
        parcel.writeString(email);
        parcel.writeString(created_by);
        parcel.writeString(user_registered);
        parcel.writeString(genre_slug);
        parcel.writeString(genre_desc);
        parcel.writeString(genre_title);
        parcel.writeString(redis_id);
        parcel.writeString(created_date);
        parcel.writeByte((byte) (sw_more ? 1 : 0));
    }
}