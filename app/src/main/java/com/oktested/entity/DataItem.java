package com.oktested.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataItem implements Parcelable {

	@SerializedName("aspect_ratio")
	public String aspectRatio;

	@SerializedName("cat_display")
	public List<CatDisplayItem> catDisplay;

	@SerializedName("social_links")
	public List<Object> socialLinks;

	@SerializedName("show")
	public Show show;

    @SerializedName("title")
    public String title;

	@SerializedName("alt_content")
	public String altContent;

	@SerializedName("tags")
	public List<String> tags;

	@SerializedName("cast_crew")
	public ArrayList<CastCrewItem> castCrew = new ArrayList<>();

	@SerializedName("srt")
	public ArrayList<SubTitleItem> srt = new ArrayList<>();

	@SerializedName("duration")
	public String duration;

	@SerializedName("topic_name")
	public String topic_name;

	@SerializedName("feature_img_port")
	public String feature_img_port;

	@SerializedName("topic_feature_img")
	public String topic_feature_img;

	@SerializedName("onexone_img")
	public String onexone_img;

	@SerializedName("topic_desc")
	public String topic_desc;

	@SerializedName("topic_slug")
	public String topic_slug;

	@SerializedName("pub_date")
	public String pubDate;

	@SerializedName("genres")
	public List<GenresItem> genres;

	@SerializedName("video_thumbnail_9x16")
	public String video_thumbnail;

	@SerializedName("feature_img")
	public String featureImg;

	@SerializedName("video_script")
	public String videoScript;

	@SerializedName("sh_heading")
	public String shHeading;

	@SerializedName("_id")
	public String id;

    @SerializedName("category")
    public List<String> category;

    @SerializedName("slug")
    public String slug;

    @SerializedName("nextVideo")
    public String nextVideo;

    @SerializedName("nsfw")
    public int nsfw;

    @SerializedName("interactive")
    public boolean interactive;

	@SerializedName("sw_more")
	public boolean sw_more;

    public long playedVideo;

	protected DataItem(Parcel in) {
		show = in.readParcelable(Show.class.getClassLoader());
		aspectRatio = in.readString();
		title = in.readString();
		altContent = in.readString();
		tags = in.createStringArrayList();
		duration = in.readString();
		topic_name = in.readString();
		feature_img_port = in.readString();
		topic_feature_img = in.readString();
		topic_desc = in.readString();
		topic_slug = in.readString();
		pubDate = in.readString();
		video_thumbnail = in.readString();
		onexone_img = in.readString();
		featureImg = in.readString();
		videoScript = in.readString();
		shHeading = in.readString();
		id = in.readString();
		category = in.createStringArrayList();
		slug = in.readString();
		nextVideo = in.readString();
		nsfw = in.readInt();
		interactive = in.readByte() != 0;
		sw_more = in.readByte() != 0;
		playedVideo = in.readLong();
		in.readTypedList(castCrew, CastCrewItem.CREATOR);
		in.readTypedList(srt, SubTitleItem.CREATOR);
	}

	public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
		@Override
		public DataItem createFromParcel(Parcel in) {
			return new DataItem(in);
		}

		@Override
		public DataItem[] newArray(int size) {
			return new DataItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(show, flags);
		dest.writeString(aspectRatio);
		dest.writeString(title);
		dest.writeString(altContent);
		dest.writeStringList(tags);
		dest.writeString(duration);
		dest.writeString(topic_name);
		dest.writeString(feature_img_port);
		dest.writeString(topic_feature_img);
		dest.writeString(topic_desc);
		dest.writeString(topic_slug);
		dest.writeString(pubDate);
		dest.writeString(video_thumbnail);
		dest.writeString(onexone_img);
		dest.writeString(featureImg);
		dest.writeString(videoScript);
		dest.writeString(shHeading);
		dest.writeString(id);
		dest.writeStringList(category);
		dest.writeString(slug);
		dest.writeString(nextVideo);
		dest.writeInt(nsfw);
		dest.writeByte((byte) (interactive ? 1 : 0));
		dest.writeByte((byte) (sw_more ? 1 : 0));
		dest.writeLong(playedVideo);
		dest.writeTypedList(castCrew);
		dest.writeTypedList(srt);
	}
}