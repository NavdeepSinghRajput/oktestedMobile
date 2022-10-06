package com.oktested.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuizStructureListModel {

    @SerializedName("section_type")
    @Expose
    public String section_type;

    @SerializedName("value")
    public QuizSectionValue value;

    public class QuizSectionValue {

        @SerializedName("slug")
        public String slug;

        @SerializedName("section_title")
        public String section_title;
    }
}
