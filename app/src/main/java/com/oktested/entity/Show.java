package com.oktested.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Show implements Parcelable {

	@SerializedName("feature_img_land")
	public String featureImgLand;

	@SerializedName("feature_img_port")
	public String featureImgPort;

	@SerializedName("topic_feature_img")
	public String topicFeatureImg;

	@SerializedName("topic")
	public String topic;

	@SerializedName("topic_display")
	public TopicDisplay topicDisplay;

	protected Show(Parcel in) {
		topicDisplay = in.readParcelable(TopicDisplay.class.getClassLoader());
		featureImgLand = in.readString();
		featureImgPort = in.readString();
		topicFeatureImg = in.readString();
		topic = in.readString();
	}

	public static final Creator<Show> CREATOR = new Creator<Show>() {
		@Override
		public Show createFromParcel(Parcel in) {
			return new Show(in);
		}

		@Override
		public Show[] newArray(int size) {
			return new Show[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeParcelable(topicDisplay, i);
		parcel.writeString(featureImgLand);
		parcel.writeString(featureImgPort);
		parcel.writeString(topicFeatureImg);
		parcel.writeString(topic);
	}
}