package com.oktested.notification.model;

import com.google.gson.annotations.SerializedName;

public class NotifcationModelItem {
        @SerializedName("id")
        public String id;
        @SerializedName("message_title")
        public String message_title;
        @SerializedName("message_body")
        public String message_body;
        @SerializedName("data_message")
        public DataMessge data_message;
        @SerializedName("send_at")
        public String send_at;
        @SerializedName("send_by")
        public String send_by;
        @SerializedName("send_to")
        public String send_to;
        @SerializedName("type")
        public String type;
        @SerializedName("action_type")
        public String action_type;
        @SerializedName("action_msg")
        public String action_msg;
        @SerializedName("is_read")
        public int is_read;
        @SerializedName("status")
        public int status;

        public class DataMessge {

            @SerializedName("request_accepted_by")
            public String request_accepted_by;
            @SerializedName("request_send_by")
            public String request_send_by;
            @SerializedName("contact")
            public String contact;
            @SerializedName("connect_type")
            public String connect_type;
            @SerializedName("image")
            public String image;
            @SerializedName("message_body")
            public String message_body;
            @SerializedName("message_title")
            public String message_title;
        }
    }