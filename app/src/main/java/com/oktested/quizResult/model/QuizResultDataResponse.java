package com.oktested.quizResult.model;

import com.oktested.core.model.BaseResponse;

public class QuizResultDataResponse extends BaseResponse {

    public String user_id;
    public String user_name;
    public String user_pic;
    public int total_score;
    public float avg_accuracy;
    public String avg_time;
    public String quiz_title;
    public String quiz_feature_img;
    public int level_count;
    public int level_min_count;
    public int level_max_count;
    public int level_current_count;
    public String level_url;
}