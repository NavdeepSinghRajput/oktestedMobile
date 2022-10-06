package com.oktested.playSolo.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;

public interface PlaySoloView extends MasterView {
    void setPlaySoloResponse(QuizStartResponse quizStartResponse);

    void insertQuizDataResponse(InsertQuizQuestionResponse insertQuizQuestionResponse);
}