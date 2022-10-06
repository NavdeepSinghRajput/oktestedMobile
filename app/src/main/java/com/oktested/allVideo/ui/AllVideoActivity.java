package com.oktested.allVideo.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.allVideo.adapter.AllVideoAdapter;
import com.oktested.allVideo.presenter.AllVideoPresenter;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.DataItem;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;

import java.util.ArrayList;

public class AllVideoActivity extends AppCompatActivity implements AllVideoView {

    private AllVideoPresenter allVideoPresenter;
    private AllVideoAdapter allVideoAdapter;
    private String screenName, offset, slug;
    private boolean dataLoaded = true, firstTime = true;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_video);
        context = this;
        allVideoPresenter = new AllVideoPresenter(context, this);

        TextView titleTV = findViewById(R.id.titleTV);
        titleTV.setText(getIntent().getExtras().getString("sectionTitle"));
        screenName = getIntent().getExtras().getString("screenName");
        offset = getIntent().getExtras().getString("offset");
        ArrayList<DataItem> dataItemList = getIntent().getParcelableArrayListExtra("dataItemList");

        if (dataItemList != null && dataItemList.size() > 0 && dataItemList.get(0).show != null && dataItemList.get(0).show.topicDisplay != null && Helper.isContainValue(dataItemList.get(0).show.topicDisplay.topicSlug)) {
            slug = dataItemList.get(0).show.topicDisplay.topicSlug;
        }

        RecyclerView allVideoRV = findViewById(R.id.allVideoRV);
        allVideoRV.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        allVideoRV.setLayoutManager(mLayoutManager);
        allVideoRV.setItemAnimator(new DefaultItemAnimator());

        allVideoAdapter = new AllVideoAdapter(context, dataItemList);
        allVideoRV.setAdapter(allVideoAdapter);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        // For fix the issue of paging api not call due to less content on screen
        if (screenName.equalsIgnoreCase(AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT) && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1") && firstTime) {
            firstTime = false;
            if (Helper.isNetworkAvailable(context)) {
                allVideoPresenter.callAppExclusivePagingApi(offset);
            }
        }

        allVideoRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            if (screenName.equalsIgnoreCase(AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT)) {
                                allVideoPresenter.callAppExclusivePagingApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_RECENTLY_ADDED_LAYOUT)) {
                                allVideoPresenter.callRecentlyAddedPagingApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_TRENDING_LAYOUT)) {
                                allVideoPresenter.callTrendingPagingApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_MOST_VIEWED_LAYOUT)) {
                                allVideoPresenter.callMostViewedPagingApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_MORE_SHOWS_LAYOUT)) {
                                allVideoPresenter.callShowsVideoPagingApi(slug, offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_SW_VIDEOS_LAYOUT)) {
                                allVideoPresenter.callScoopWhoopVideoPagingApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT)) {
                                allVideoPresenter.callScoopWhoopShowVideoApi(slug, offset);
                            }
                        }
                    }
                }
            }
        });

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.TOP_BANNER_AD)) {
            loadTopAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.TOP_BANNER_AD));
        }
    }

    @Override
    protected void onDestroy() {
        allVideoPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void setAppExclusiveResponse(AppExclusiveResponse appExclusiveResponse) {
        if (appExclusiveResponse != null && appExclusiveResponse.data != null && appExclusiveResponse.data.size() > 0) {
            dataLoaded = true;
            offset = appExclusiveResponse.next_offset;
            allVideoAdapter.setMoreVideosData(appExclusiveResponse.data);
        }
    }

    @Override
    public void setRecentlyAddedResponse(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            dataLoaded = true;
            offset = videoListResponse.next_offset;
            allVideoAdapter.setMoreVideosData(videoListResponse.data);
        }
    }

    @Override
    public void setTrendingVideoResponse(TrendingVideoResponse trendingVideoResponse) {
        if (trendingVideoResponse != null && trendingVideoResponse.data != null && trendingVideoResponse.data.size() > 0) {
            dataLoaded = true;
            offset = trendingVideoResponse.next_offset;
            allVideoAdapter.setMoreVideosData(trendingVideoResponse.data);
        }
    }

    @Override
    public void setMostViewedVideoResponse(MostViewedVideoResponse mostViewedVideoResponse) {
        if (mostViewedVideoResponse != null && mostViewedVideoResponse.data != null && mostViewedVideoResponse.data.size() > 0) {
            dataLoaded = true;
            offset = mostViewedVideoResponse.next_offset;
            allVideoAdapter.setMoreVideosData(mostViewedVideoResponse.data);
        }
    }

    @Override
    public void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse) {
        if (showsVideoResponse != null && showsVideoResponse.data != null && showsVideoResponse.data.size() > 0) {
            dataLoaded = true;
            offset = showsVideoResponse.next_offset;
            allVideoAdapter.setMoreVideosData(showsVideoResponse.data);
        }
    }

    @Override
    public void setScoopWhoopVideoResponse(ScoopWhoopVideoResponse scoopWhoopVideoResponse) {
        if (scoopWhoopVideoResponse != null && scoopWhoopVideoResponse.data != null && scoopWhoopVideoResponse.data.size() > 0) {
            dataLoaded = true;
            offset = scoopWhoopVideoResponse.next_offset;
            allVideoAdapter.setMoreVideosData(scoopWhoopVideoResponse.data);
        }
    }

    @Override
    public void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse) {
        if (scoopWhoopShowVideoResponse != null && scoopWhoopShowVideoResponse.data != null && scoopWhoopShowVideoResponse.data.size() > 0) {
            dataLoaded = true;
            offset = scoopWhoopShowVideoResponse.next_offset;
            allVideoAdapter.setMoreVideosData(scoopWhoopShowVideoResponse.data);
        }
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout bottomAdView = findViewById(R.id.bottomAdView);
            MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }

    private void loadTopAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout topAdView = findViewById(R.id.topAdView);
            MyBannerAd.getInstance().getAd(context, topAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }
}