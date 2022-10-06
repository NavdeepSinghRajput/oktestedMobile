package com.oktested.videoCall.model;

import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class QuestionWiseResultResponse extends BaseResponse {

    public boolean all_played;
    public ArrayList<QuestionWiseResultDataResponse> data;

    public class QuestionWiseResultDataResponse {
        public String status;
        public int user_channel_id;
        public String current_qus_status;
        public int total_score;
    }
}