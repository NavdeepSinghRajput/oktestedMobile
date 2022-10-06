package com.oktested.search.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.search.model.SearchHistoryResponse;

public interface SearchView extends MasterView {

    void setSearchHistoryResponse(SearchHistoryResponse searchHistoryResponse);

    void setUserSearchResponse(VideoListResponse videoListResponse);

    void setSearchQuizResponse(SearchQuizResponse searchQuizResponse);
}