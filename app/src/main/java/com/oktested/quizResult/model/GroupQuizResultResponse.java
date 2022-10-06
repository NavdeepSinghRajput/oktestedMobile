package com.oktested.quizResult.model;

import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class GroupQuizResultResponse extends BaseResponse {

    public int winner_count;
    public String quiz_title;
    public  String quiz_feature_img;
    public ArrayList<GroupQuizResultDataResponse> data;

    public class GroupQuizResultDataResponse extends BaseResponse {
        public String user_name;
        public String user_pic;
        public int total_score;
        public String avg_time;
        public int avg_accuracy;
        public int max_score;
        public int rank;
    }
}