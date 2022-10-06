package com.oktested.home.model;

import com.google.gson.annotations.SerializedName;

public class ContactListModelItem {
        @SerializedName("id")
        public String id;
        @SerializedName("picture")
        public String picture;
        @SerializedName("name")
        public String name;
        @SerializedName("app_installed")
        public boolean app_installed;
        @SerializedName("contact")
        public String contact;
        @SerializedName("status")
        public String status;
    }