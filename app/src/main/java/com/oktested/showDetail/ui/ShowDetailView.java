package com.oktested.showDetail.ui;

import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;

public interface ShowDetailView extends MasterView {
    void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse);

    void setUnFavResponse(UnFavouriteResponse unFavouriteResponse);

    void setFavResponse(FavouriteResponse favouriteResponse);

    void getUserData(GetUserResponse getUserResponse);

    void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse);
}