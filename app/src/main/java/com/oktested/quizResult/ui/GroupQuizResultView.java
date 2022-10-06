package com.oktested.quizResult.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.quizResult.model.GroupQuizResultResponse;

public interface GroupQuizResultView extends MasterView {
    void setGroupQuizResultResponse(GroupQuizResultResponse groupQuizResultResponse);
}