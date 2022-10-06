package com.oktested.browseShow.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ShowsResponse;

public interface BrowseShowsView extends MasterView {

    void setShowsResponse(ShowsResponse showsResponse);

    void setScoopWhoopShowsResponse(ScoopWhoopShowsResponse scoopWhoopShowsResponse);
}