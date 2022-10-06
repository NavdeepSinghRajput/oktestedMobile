package com.oktested.allQuiz.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;

public interface AllQuizView extends MasterView {

    void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse);

    void setLatestQuizResponse(LatestQuizResponse latestQuizResponse);

    void setCategoryQuizResponse(CategoryQuizResponse categoryQuizResponse);

    void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse);

    void setRecentPlayedQuizResponse(RecentPlayedQuizResponse recentPlayedQuizResponse);
}