package com.oktested.home.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.oktested.R;
import com.oktested.home.adapter.VideosAdapter;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.EditorPickResponse;
import com.oktested.home.model.HomeStructureListModel;
import com.oktested.home.model.HomeStructureResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.home.presenter.VideosPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideosFragment extends Fragment implements VideosView {

    private Context context;
    private RecyclerView videosRV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private VideosPresenter videosPresenter;
    private SpinKitView spinKitView;
    private VideosAdapter videosAdapter;
    private Map<Integer, Object> sectionMap = new HashMap<>();
    private int totalShowsCount = AppConstants.SECTION_MORE_SHOWS;
    private int totalScoopWhoopShowsCount = AppConstants.SECTION_SW_SHOWS_VIDEO;
    private int totalBannerCount = AppConstants.SECTION_BANNER;
    private int totalBannerAdCount = AppConstants.SECTION_BANNER_AD;
    private ArrayList<Integer> moreShowsCountAL = new ArrayList<>();
    private ArrayList<Integer> swShowsVideoCountAL = new ArrayList<>();
    private ArrayList<Integer> bannerCountAL = new ArrayList<>();
    private ArrayList<Integer> bannerAdCountAL = new ArrayList<>();
    private int moreShowsCount = 0, swShowsVideoCount = 0;
    private ArrayList<HomeStructureListModel> homeStructureAL;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_videos, container, false);

        videosPresenter = new VideosPresenter(context, this);
        spinKitView = root_view.findViewById(R.id.spinKit);

        videosRV = root_view.findViewById(R.id.videosRV);
        videosRV.setNestedScrollingEnabled(false);
        videosRV.setHasFixedSize(true);
        videosRV.setLayoutManager(new LinearLayoutManager(context));
        videosRV.setItemAnimator(new DefaultItemAnimator());

        callHomeStructureApi();
       //getHomeStructureFromFile();

        swipeRefreshLayout = root_view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isNetworkAvailable(context)) {
                    sectionMap.clear();
                    moreShowsCountAL.clear();
                    swShowsVideoCountAL.clear();
                    bannerCountAL.clear();
                    bannerAdCountAL.clear();
                    moreShowsCount = 0;
                    swShowsVideoCount = 0;
                    totalShowsCount = AppConstants.SECTION_MORE_SHOWS;
                    totalScoopWhoopShowsCount = AppConstants.SECTION_SW_SHOWS_VIDEO;
                    totalBannerCount = AppConstants.SECTION_BANNER;
                    totalBannerAdCount = AppConstants.SECTION_BANNER_AD;
                    swipeRefreshLayout.setRefreshing(false);
                    callHomeStructureApi();
                    //getHomeStructureFromFile();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        });

        return root_view;
    }

    private void getHomeStructureFromFile() {
        String myJson = inputStreamToString(context.getResources().openRawResource(R.raw.dynamichome));
        HomeStructureResponse homeStructureResponse = new Gson().fromJson(myJson, HomeStructureResponse.class);
        if (homeStructureResponse != null && homeStructureResponse.data != null && homeStructureResponse.data.size() > 0) {
            HomeStructureListModel homeStructureListModel = new HomeStructureListModel();
            homeStructureListModel.section_type = AppConstants.SECTION_EDITOR_PICK_LAYOUT;
            homeStructureResponse.data.add(0, homeStructureListModel);
            homeStructureAL = homeStructureResponse.data;

            for (int i = 0; i < homeStructureResponse.data.size(); i++) {
                if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_EDITOR_PICK_LAYOUT)) {
                    showLoader();
                    videosPresenter.callEditorPickApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_BANNER_LAYOUT)) {
                    int count = totalBannerCount++;
                    bannerCountAL.add(count);
                    sectionMap.put(count, homeStructureResponse.data.get(i).value);
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT)) {
                    showLoader();
                    videosPresenter.callAppExclusiveApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_RECENTLY_ADDED_LAYOUT)) {
                    showLoader();
                    videosPresenter.callRecentlyAddedApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_TRENDING_LAYOUT)) {
                    showLoader();
                    videosPresenter.callTrendingApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_MOST_VIEWED_LAYOUT)) {
                    showLoader();
                    videosPresenter.callMostViewedApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SHOWS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callShowsApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_ANCHORS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callAnchorsApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_MORE_SHOWS_LAYOUT)) {
                    showLoader();
                    moreShowsCount++;
                    videosPresenter.callShowsVideoApi(homeStructureResponse.data.get(i).value.slug);
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_VIDEOS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callScoopWhoopVideoApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callScoopWhoopShowsApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT)) {
                    swShowsVideoCount++;
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_BANNER_AD_LAYOUT)) {
                    int count = totalBannerAdCount++;
                    bannerAdCountAL.add(count);
                    sectionMap.put(count, homeStructureResponse.data.get(i).value);
                }
            }
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private void callHomeStructureApi() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            videosPresenter.callHomeStructure();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void showLoader() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setHomeStructureResponse(HomeStructureResponse homeStructureResponse) {
        if (homeStructureResponse != null && homeStructureResponse.data != null && homeStructureResponse.data.size() > 0) {
            HomeStructureListModel homeStructureListModel = new HomeStructureListModel();
            homeStructureListModel.section_type = AppConstants.SECTION_EDITOR_PICK_LAYOUT;
            homeStructureResponse.data.add(0, homeStructureListModel);
            homeStructureAL = homeStructureResponse.data;

            for (int i = 0; i < homeStructureResponse.data.size(); i++) {
                if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_EDITOR_PICK_LAYOUT)) {
                    showLoader();
                    videosPresenter.callEditorPickApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_BANNER_LAYOUT)) {
                    int count = totalBannerCount++;
                    bannerCountAL.add(count);
                    sectionMap.put(count, homeStructureResponse.data.get(i).value);
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT)) {
                    showLoader();
                    videosPresenter.callAppExclusiveApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_RECENTLY_ADDED_LAYOUT)) {
                    showLoader();
                    videosPresenter.callRecentlyAddedApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_TRENDING_LAYOUT)) {
                    showLoader();
                    videosPresenter.callTrendingApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_MOST_VIEWED_LAYOUT)) {
                    showLoader();
                    videosPresenter.callMostViewedApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SHOWS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callShowsApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_ANCHORS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callAnchorsApi();
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_MORE_SHOWS_LAYOUT)) {
                    showLoader();
                    moreShowsCount++;
                    videosPresenter.callShowsVideoApi(homeStructureResponse.data.get(i).value.slug);
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_VIDEOS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callScoopWhoopVideoApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_LAYOUT)) {
                    showLoader();
                    videosPresenter.callScoopWhoopShowsApi("0");
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT)) {
                    swShowsVideoCount++;
                } else if (homeStructureResponse.data.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_BANNER_AD_LAYOUT)) {
                    int count = totalBannerAdCount++;
                    bannerAdCountAL.add(count);
                    sectionMap.put(count, homeStructureResponse.data.get(i).value);
                }
            }
        }
    }

    @Override
    public void setEditorPickResponse(EditorPickResponse editorPickResponse) {
        if (editorPickResponse != null && editorPickResponse.data != null) {
            sectionMap.put(AppConstants.SECTION_EDITOR_PICK, editorPickResponse.data);
        }
    }

    @Override
    public void setAppExclusiveResponse(AppExclusiveResponse appExclusiveResponse) {
        if (appExclusiveResponse != null && appExclusiveResponse.data != null && appExclusiveResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_APP_EXCLUSIVE, appExclusiveResponse);
        }
    }

    @Override
    public void setRecentlyAddedResponse(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_RECENTLY_ADDED, videoListResponse);
        }
    }

    @Override
    public void setTrendingVideoResponse(TrendingVideoResponse trendingVideoResponse) {
        if (trendingVideoResponse != null && trendingVideoResponse.data != null && trendingVideoResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_TRENDING, trendingVideoResponse);
        }
    }

    @Override
    public void setMostViewedVideoResponse(MostViewedVideoResponse mostViewedVideoResponse) {
        if (mostViewedVideoResponse != null && mostViewedVideoResponse.data != null && mostViewedVideoResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_MOST_VIEWED, mostViewedVideoResponse);
        }
    }

    @Override
    public void setShowsResponse(ShowsResponse showsResponse) {
        if (showsResponse != null && showsResponse.data != null && showsResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_SHOWS, showsResponse);
        }
    }

    @Override
    public void setAnchorsResponse(AnchorsResponse anchorsResponse) {
        if (anchorsResponse != null && anchorsResponse.data != null && anchorsResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_ANCHORS, anchorsResponse.data);
            DataHolder.getInstance().anchorList = anchorsResponse.data;
        }
    }

    @Override
    public void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse) {
        if (showsVideoResponse != null && showsVideoResponse.data != null && showsVideoResponse.data.size() > 0) {
            int count = totalShowsCount++;
            sectionMap.put(count, showsVideoResponse);
            moreShowsCountAL.add(count);

            if (moreShowsCountAL.size() == moreShowsCount) {
                if (swShowsVideoCount == 0) {
                    hideLoader();
                    videosAdapter = new VideosAdapter(context, homeStructureAL, sectionMap, moreShowsCountAL, swShowsVideoCountAL, bannerCountAL, bannerAdCountAL);
                    videosRV.setAdapter(videosAdapter);
                } else {
                    for (int i = 0; i < homeStructureAL.size(); i++) {
                        if (homeStructureAL.get(i).section_type.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT)) {
                            showLoader();
                            videosPresenter.callScoopWhoopShowVideoApi(homeStructureAL.get(i).value.slug, "0");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setScoopWhoopVideoResponse(ScoopWhoopVideoResponse scoopWhoopVideoResponse) {
        if (scoopWhoopVideoResponse != null && scoopWhoopVideoResponse.data != null && scoopWhoopVideoResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_SW_VIDEOS, scoopWhoopVideoResponse);
        }
    }

    @Override
    public void setScoopWhoopShowsResponse(ScoopWhoopShowsResponse scoopWhoopShowsResponse) {
        if (scoopWhoopShowsResponse != null && scoopWhoopShowsResponse.data != null && scoopWhoopShowsResponse.data.size() > 0) {
            sectionMap.put(AppConstants.SECTION_SW_SHOWS, scoopWhoopShowsResponse);
        }
    }

    @Override
    public void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse) {
        if (scoopWhoopShowVideoResponse != null && scoopWhoopShowVideoResponse.data != null && scoopWhoopShowVideoResponse.data.size() > 0) {
            int count = totalScoopWhoopShowsCount++;
            sectionMap.put(count, scoopWhoopShowVideoResponse);
            swShowsVideoCountAL.add(count);
            if (swShowsVideoCountAL.size() == swShowsVideoCount) {
                hideLoader();
                videosAdapter = new VideosAdapter(context, homeStructureAL, sectionMap, moreShowsCountAL, swShowsVideoCountAL, bannerCountAL, bannerAdCountAL);
                videosRV.setAdapter(videosAdapter);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (videosPresenter != null) {
            videosPresenter.onDestroyedView();
        }
        super.onDestroy();
    }
}