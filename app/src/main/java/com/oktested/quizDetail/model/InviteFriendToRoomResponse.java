package com.oktested.quizDetail.model;

import com.oktested.core.model.BaseResponse;

public class InviteFriendToRoomResponse extends BaseResponse {

    public String notification_msg;
    public ChannelData channel_data;

    public class ChannelData {
        public String agora_access_token;
        public String _id;
        public int user_channel_id;
        public boolean is_channel_admin;
    }
}