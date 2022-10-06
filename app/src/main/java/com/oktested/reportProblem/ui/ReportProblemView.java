package com.oktested.reportProblem.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.reportProblem.model.ReportProblemResponse;

public interface ReportProblemView extends MasterView {
    void showResponse(ReportProblemResponse reportProblemResponse);
}