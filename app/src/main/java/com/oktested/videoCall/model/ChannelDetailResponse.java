package com.oktested.videoCall.model;

import com.oktested.core.model.BaseResponse;

import java.util.ArrayList;

public class ChannelDetailResponse extends BaseResponse {
    public ChannelDetailResponseData data;

    public class ChannelDetailResponseData {
        public int user_channel_id;
        public boolean is_channel_admin;
        public boolean is_ready;
        public boolean is_all_ready;
        public ArrayList<MembersInfo> members_info;
    }

    public class MembersInfo {
        public String user_id;
        public int uid;
        public String name;
        public String profile_pic;
        public boolean is_ready;
        public boolean is_channel_admin;
        public boolean is_camera_enable;
    }
}