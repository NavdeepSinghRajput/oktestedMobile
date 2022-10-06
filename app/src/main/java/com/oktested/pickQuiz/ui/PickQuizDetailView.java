package com.oktested.pickQuiz.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.pickQuiz.model.SelectQuizResponse;
import com.oktested.pickQuiz.model.SuggestQuizResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;

public interface PickQuizDetailView extends MasterView {

    void setQuizDetailResponse(QuizDetailResponse quizDetailResponse);

    void selectQuizResponse(SelectQuizResponse selectQuizResponse);

    void suggestQuizResponse(SuggestQuizResponse suggestQuizResponse);
}