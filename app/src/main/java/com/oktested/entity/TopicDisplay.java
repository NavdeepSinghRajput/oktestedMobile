package com.oktested.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TopicDisplay implements Parcelable {

	@SerializedName("topic_display")
	public String topicDisplay;

	@SerializedName("topic_slug")
	public String topicSlug;

	protected TopicDisplay(Parcel in) {
		topicDisplay = in.readString();
		topicSlug = in.readString();
	}

	public static final Creator<TopicDisplay> CREATOR = new Creator<TopicDisplay>() {
		@Override
		public TopicDisplay createFromParcel(Parcel in) {
			return new TopicDisplay(in);
		}

		@Override
		public TopicDisplay[] newArray(int size) {
			return new TopicDisplay[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(topicDisplay);
		parcel.writeString(topicSlug);
	}
}