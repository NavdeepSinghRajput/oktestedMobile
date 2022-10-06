package com.oktested.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;
import com.oktested.entity.Likes;

import java.util.ArrayList;

public class GetUserDataresponse extends BaseResponse {

    public String gender;
    public ArrayList<String> subscribe;
    public String lastLogin;
    public String name;
    public String id;
    public String display_name;
    public ArrayList<String> follow;
    public ArrayList<String> favourite;
    public ArrayList<String> favourite_shows;
    public ArrayList<String> firebase_token;
    public String email;
    public String picture;
    public String avatar;
    public Likes likes;
    public String timestamp;
    public String points;
    public String quiz_played;
    public String mobile;
    public String dob;
    public Social social;
    public String last_login_type;
    public boolean is_blocked;
    public String notification;
    public String invite_link;
    public boolean is_quiz_enable;

    public class Social {

        public Google Google;
        public Facebook Facebook;

        public class Google {
            public String id;
        }

        public class Facebook {
            public String id;
        }
    }
}