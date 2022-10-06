package com.oktested.favourite.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.VideoListResponse;

public interface FavouriteView extends MasterView {

    void getFavouriteVideos(VideoListResponse videoListResponse);

    void getFavouriteShows(ShowsResponse favouriteShowsResponse);

    void getFavouriteAnchors(AnchorsResponse anchorDetailResponse);
}