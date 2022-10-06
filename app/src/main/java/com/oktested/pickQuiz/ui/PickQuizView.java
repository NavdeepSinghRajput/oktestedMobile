package com.oktested.pickQuiz.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;

public interface PickQuizView extends MasterView {

    void setQuizViewResponse(CategoryQuizResponse categoryQuizResponse);

    void setTrendingQuizResponse(TrendingQuizResponse tredingQuizResponse);

    void setLatestQuizResponse(LatestQuizResponse latestQuizResponse);

    void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse);

    void setSearchQuizResponse(SearchQuizResponse searchQuizResponse);
}