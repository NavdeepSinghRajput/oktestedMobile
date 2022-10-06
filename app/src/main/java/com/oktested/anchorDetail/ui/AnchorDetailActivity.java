package com.oktested.anchorDetail.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.activities.WebViewActivity;
import com.oktested.anchorDetail.adapter.AnchorVideoAdapter;
import com.oktested.anchorDetail.presenter.AnchorDetailPresenter;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.DataItem;
import com.oktested.favourite.ui.FavouriteFragment;
import com.oktested.home.model.AnchorsListModel;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.EndlessParentScrollListener;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;

import java.util.ArrayList;

public class AnchorDetailActivity extends AppCompatActivity implements AnchorDetailView, View.OnClickListener {

    private Context context;
    private ImageView anchorIV, followIV;
    private NestedScrollView nestedScrollView;
    private RelativeLayout followRL, instagramRL, twitterRL;
    private TextView anchorNameTV, anchorDescTV, followAnchorNameTV, videoFeatureTV;
    private AnchorVideoAdapter anchorVideoAdapter;
    private AnchorDetailPresenter anchorDetailPresenter;
    ArrayList<DataItem> dataItemList = new ArrayList<>();
    private String twitterUrl, instagramUrl, userName, offset;
    private boolean isFollow, dataLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_detail);
        context = this;

        inUi();
        setAdapter();

        AnchorsListModel anchorsListModel = (AnchorsListModel) getIntent().getSerializableExtra("anchorModel");
        setAnchorBasicData(anchorsListModel);
        callAnchorVideoApi(anchorsListModel.username);

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MIDDLE_BANNER_AD)) {
            loadMiddleAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MIDDLE_BANNER_AD));
        }
    }

    private void inUi() {
        anchorDetailPresenter = new AnchorDetailPresenter(context, this);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        anchorIV = findViewById(R.id.anchorIV);
        followIV = findViewById(R.id.followIV);

        followRL = findViewById(R.id.followRL);
        instagramRL = findViewById(R.id.instagramRL);
        twitterRL = findViewById(R.id.twitterRL);

        anchorNameTV = findViewById(R.id.anchorNameTV);
        anchorDescTV = findViewById(R.id.anchorDescTV);
        followAnchorNameTV = findViewById(R.id.followAnchorNameTV);
        videoFeatureTV = findViewById(R.id.videoFeatureTV);

        followRL.setOnClickListener(this);
        instagramRL.setOnClickListener(this);
        twitterRL.setOnClickListener(this);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());
    }

    private void setAnchorBasicData(AnchorsListModel anchorsListModel) {
        if (Helper.isContainValue(anchorsListModel.display_name)) {
            anchorNameTV.setText(anchorsListModel.display_name);
            followAnchorNameTV.setText("Follow " + anchorsListModel.display_name);
        }

        if (Helper.isContainValue(anchorsListModel.first_name)) {
            videoFeatureTV.setText("Videos Featuring " + anchorsListModel.first_name);
        }

        if (Helper.isContainValue(anchorsListModel.about_me)) {
            anchorDescTV.setText(anchorsListModel.about_me);
        }

        if (Helper.isContainValue(anchorsListModel.profile_pic)) {
            Glide.with(context)
                    .load(anchorsListModel.profile_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(anchorIV);
        }

        if (anchorsListModel.social_links != null && anchorsListModel.social_links.size() > 0) {
            for (int i = 0; i < anchorsListModel.social_links.size(); i++) {
                if (anchorsListModel.social_links.get(i).network.equalsIgnoreCase("instagram")) {
                    instagramUrl = anchorsListModel.social_links.get(i).link;
                    instagramRL.setVisibility(View.VISIBLE);
                } else if (anchorsListModel.social_links.get(i).network.equalsIgnoreCase("twitter")) {
                    twitterRL.setVisibility(View.VISIBLE);
                    twitterUrl = anchorsListModel.social_links.get(i).link;
                }
            }
        }

        userName = anchorsListModel.username;
        setAnchorFollowIcon(DataHolder.getInstance().getUserDataresponse, userName);
    }

    private void setAnchorFollowIcon(GetUserDataresponse response, String userName) {
        if (response != null && response.follow != null && response.follow.size() > 0) {
            for (int i = 0; i < response.follow.size(); i++) {
                if (response.follow.get(i).equalsIgnoreCase(userName)) {
                    isFollow = true;
                    followRL.setBackground(getResources().getDrawable(R.drawable.circle_favourite_bg));
                    followIV.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    break;
                }
            }
        }
    }

    private void callAnchorVideoApi(String username) {
        if (Helper.isNetworkAvailable(context)) {
            anchorDetailPresenter.callActorsVideoListApi(username);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void setAdapter() {
        RecyclerView anchorVideosRV = findViewById(R.id.anchorVideosRV);
        anchorVideosRV.setNestedScrollingEnabled(false);
        anchorVideosRV.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        anchorVideosRV.setLayoutManager(mLayoutManager);
        anchorVideosRV.setItemAnimator(new DefaultItemAnimator());

        anchorVideoAdapter = new AnchorVideoAdapter(context, dataItemList);
        anchorVideosRV.setAdapter(anchorVideoAdapter);

        nestedScrollView.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (dataItemList != null && dataItemList.size() >= 9) {
                    if (dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            anchorDetailPresenter.callActorsVideoPagingListApi(userName, offset);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getVideoListResponse(VideoListResponse videoListResponse) {
        if (videoListResponse != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            dataItemList.clear();
            dataItemList = videoListResponse.data;
            dataLoaded = true;
            offset = videoListResponse.next_offset;
            videoFeatureTV.setVisibility(View.VISIBLE);
            anchorVideoAdapter.setAnchorVideoData(videoListResponse.data);
        }
    }

    @Override
    public void getUserData(GetUserResponse getUserResponse) {
        if (getUserResponse != null && getUserResponse.data != null) {
            DataHolder.getInstance().getUserDataresponse = getUserResponse.data;

            if (getUserResponse.data.follow != null && getUserResponse.data.follow.size() > 0) {
                FavouriteFragment.callAnchorListApi(getUserResponse.data.follow);
            } else {
                FavouriteFragment.hideAnchorView();
            }

            if (getUserResponse.data.follow != null && !(getUserResponse.data.follow.size() > 0) && getUserResponse.data.favourite != null && !(getUserResponse.data.favourite.size() > 0) && getUserResponse.data.favourite_shows != null && !(getUserResponse.data.favourite_shows.size() > 0)) {
                FavouriteFragment.showNoDataView();
            }
        }
    }

    @Override
    public void setFollowResponse(FollowResponse followResponse) {
        if (followResponse != null) {
            callUserDataApi();
        }
    }

    @Override
    public void setUnFollowResponse(UnFollowResponse unFollowResponse) {
        if (unFollowResponse != null) {
            callUserDataApi();
        }
    }

    @Override
    protected void onDestroy() {
        anchorDetailPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.instagramRL:
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("link", instagramUrl);
                context.startActivity(intent);
                break;

            case R.id.twitterRL:
                Intent intent1 = new Intent(context, WebViewActivity.class);
                intent1.putExtra("link", twitterUrl);
                context.startActivity(intent1);
                break;

            case R.id.followRL:
                callAnchorFollowApi();
                break;

            default:
                break;
        }
    }

    private void callUserDataApi() {
        if (Helper.isNetworkAvailable(context)) {
            anchorDetailPresenter.callUserData();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callAnchorFollowApi() {
        if (Helper.isNetworkAvailable(context)) {
            if (isFollow) {
                isFollow = false;
                followRL.setBackground(getResources().getDrawable(R.drawable.circle_gender_bg));
                followIV.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
                anchorDetailPresenter.callUnFollowApi(userName);
            } else {
                isFollow = true;
                followRL.setBackground(getResources().getDrawable(R.drawable.circle_favourite_bg));
                followIV.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                anchorDetailPresenter.callFollowApi(userName);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void loadMiddleAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout middleAdView = findViewById(R.id.middleAdView);
            MyBannerAd.getInstance().getAd(context, middleAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout bottomAdView = findViewById(R.id.bottomAdView);
            MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }
}