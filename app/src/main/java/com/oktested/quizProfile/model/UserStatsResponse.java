package com.oktested.quizProfile.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

import java.util.List;

public class UserStatsResponse extends BaseResponse {

    @SerializedName("data")
    public UserStatsData data;

    public class UserStatsData {

        @SerializedName("average_accuracy")
        public float average_accuracy;
        @SerializedName("average_time")
        public float average_time;
        @SerializedName("total_unique_quiz_played")
        public int total_unique_quiz_played;
        @SerializedName("level_count")
        public int level_count;
        @SerializedName("level_max_count")
        public int level_max_count;
        @SerializedName("level_min_count")
        public int level_min_count;
        @SerializedName("level_current_count")
        public int level_current_count;
        @SerializedName("level_url")
        public String level_url;
        @SerializedName("category_data")
        public List<CategoryQuizPlayed> category_data;

        public class CategoryQuizPlayed {
            @SerializedName("category_name")
            public String category_name;
            @SerializedName("percentage")
            public float percentage;
        }
    }
}