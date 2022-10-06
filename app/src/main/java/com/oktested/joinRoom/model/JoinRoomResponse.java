package com.oktested.joinRoom.model;

import com.oktested.core.model.BaseResponse;

public class JoinRoomResponse extends BaseResponse {
    public ChannelData channel_data;

    public class ChannelData {
        public String agora_access_token;
        public String _id;
        public int user_channel_id;
        public boolean is_channel_admin;
    }
}