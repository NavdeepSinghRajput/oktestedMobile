package com.oktested.videoPlayer.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.cast.framework.CastContext;
import com.oktested.R;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.DataItem;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;
import com.oktested.utils.NonSwipeableViewPager;
import com.oktested.videoPlayer.adapter.VideoPlayerAdapter;
import com.oktested.videoPlayer.model.SingleCommentResponse;
import com.oktested.videoPlayer.model.VideoCommentDataResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;
import com.oktested.videoPlayer.presenter.VideoPlayerActivityPresenter;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayerActivity extends AppCompatActivity implements Player.EventListener, VideoPlayerActivityView {

    private NonSwipeableViewPager videoVP;
    private VideoPlayerAdapter videoPlayerAdapter;
    private Context context;
    private ScreenRotation screenRotation;
    private SpinKitView spinKitView;
    private CastContext castContext;
    private VideoPlayerActivityPresenter videoPlayerActivityPresenter;
    private RelativeLayout bottomAdView;
    private ArrayList<DataItem> dataItemAL = new ArrayList<>();
    private String commentId = "", videoId;
    private boolean isBottomAdLoaded = false;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof ScreenRotation) {
            screenRotation = (ScreenRotation) fragment;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        context = this;

        castContext = CastContext.getSharedInstance(context);
        videoPlayerActivityPresenter = new VideoPlayerActivityPresenter(context, this);
        spinKitView = findViewById(R.id.spinKit);
        bottomAdView = findViewById(R.id.bottomAdView);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("videoId")) {
            if (getIntent().getExtras().containsKey("commentId")) {
                commentId = getIntent().getExtras().getString("commentId");
            }
            videoId = getIntent().getExtras().getString("videoId");
            ArrayList<String> videoIdList = new ArrayList<>();
            videoIdList.add(videoId);
            callVideoDataApi(videoIdList);

            // For load ads in hash map when open player via notification
            if (DataHolder.getInstance().adSettingValueHashMap.size() == 0) {
                if (DataHolder.getInstance().adSettingResponse != null && DataHolder.getInstance().adSettingResponse.enable && DataHolder.getInstance().adSettingResponse.data != null && DataHolder.getInstance().adSettingResponse.data.size() > 0) {
                    for (int i = 0; i < DataHolder.getInstance().adSettingResponse.data.size(); i++) {
                        DataHolder.getInstance().adSettingValueHashMap.put(DataHolder.getInstance().adSettingResponse.data.get(i).ad_type, DataHolder.getInstance().adSettingResponse.data.get(i).value);
                    }
                }
            }
        } else {
            ArrayList<DataItem> dataItemList = getIntent().getParcelableArrayListExtra("dataItemList");
            int currentItem = getIntent().getExtras().getInt("position");

            videoVP = findViewById(R.id.videoVP);
            videoVP.setOffscreenPageLimit(0);
            videoPlayerAdapter = new VideoPlayerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, dataItemList, context, currentItem, castContext, null);
            videoVP.setAdapter(videoPlayerAdapter);
        }

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (screenRotation != null) {
                        screenRotation.setPlayerControlDimension();
                    }
                } else {
                    finish();
                }
            }
        });

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
    }

    public void callVideoDataApi(ArrayList<String> videoIdAL) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            videoPlayerActivityPresenter.callSingleVideoDataApi(videoIdAL);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    public void callCommentDetailApi(String commentId, String videoId) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            videoPlayerActivityPresenter.getCommentDetailedData(commentId, videoId);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (screenRotation != null) {
                screenRotation.setPlayerControlDimension();
            }
        } else {
            super.onBackPressed();
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
    public void getVideoData(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            if (Helper.isContainValue(commentId)) {
                dataItemAL.addAll(videoListResponse.data);
                callCommentDetailApi(commentId, videoId);
            } else {
                videoVP = findViewById(R.id.videoVP);
                videoVP.setOffscreenPageLimit(0);
                videoPlayerAdapter = new VideoPlayerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, videoListResponse.data, context, 0, castContext, null);
                videoVP.setAdapter(videoPlayerAdapter);
            }
        }
    }

    @Override
    public void getCommentDetailData(SingleCommentResponse singleCommentResponse) {
        if (singleCommentResponse != null && Helper.isContainValue(singleCommentResponse.status) && singleCommentResponse.status.equalsIgnoreCase("success")) {
            if (singleCommentResponse.data != null) {
                videoVP = findViewById(R.id.videoVP);
                videoVP.setOffscreenPageLimit(0);
                videoPlayerAdapter = new VideoPlayerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, dataItemAL, context, 0, castContext, singleCommentResponse.data);
                videoVP.setAdapter(videoPlayerAdapter);
            }
        }
    }

    public interface ScreenRotation {
        void setPlayerControlDimension();
    }

    @Override
    public void onDestroy() {
        if (videoPlayerActivityPresenter != null) {
            videoPlayerActivityPresenter.onDestroyedView();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomAdView.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomAdView.setVisibility(View.VISIBLE);
            if (!isBottomAdLoaded) {
                if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
                    loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
                }
            }
        }
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                isBottomAdLoaded = true;
                MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
            } else {
                bottomAdView.setVisibility(View.GONE);
            }
        }
    }
}