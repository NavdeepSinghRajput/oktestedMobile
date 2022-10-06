package com.oktested.quizResult.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.quizResult.model.QuizResultResponse;
import com.oktested.videoCall.model.RecommendedQuizResponse;

public interface QuizResultView extends MasterView {
    void setQuizResultResponse(QuizResultResponse quizResultResponse);

    void setRecommendedQuizResponse(RecommendedQuizResponse recommendedQuizResponse);
}