package com.oktested.reportProblem.model;

import com.google.gson.annotations.SerializedName;
import com.oktested.core.model.BaseResponse;

public class ReportProblemDataResponse extends BaseResponse {

    @SerializedName("id")
    public String id;
}