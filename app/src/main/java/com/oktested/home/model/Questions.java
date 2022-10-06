package com.oktested.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Questions implements Parcelable {
    @SerializedName("timer")
    @Expose
    public int timer;

    @SerializedName("correct_ans")
    @Expose
    public int correct_ans;

    @SerializedName("score")
    @Expose
    public int score;

    @SerializedName("choices")
    @Expose
    public ArrayList<QuestionOption> choices = new ArrayList<>();

    @SerializedName("question")
    @Expose
    public Question question;

    protected Questions(Parcel in) {
        timer = in.readInt();
        correct_ans = in.readInt();
        score = in.readInt();
        in.readTypedList(choices, QuestionOption.CREATOR);
        question = in.readParcelable(Question.class.getClassLoader());
    }

    public static final Creator<Questions> CREATOR = new Creator<Questions>() {
        @Override
        public Questions createFromParcel(Parcel in) {
            return new Questions(in);
        }

        @Override
        public Questions[] newArray(int size) {
            return new Questions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(timer);
        parcel.writeInt(correct_ans);
        parcel.writeInt(score);
        parcel.writeTypedList(choices);
        parcel.writeParcelable(question, i);
    }
}