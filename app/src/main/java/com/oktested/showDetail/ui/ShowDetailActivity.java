package com.oktested.showDetail.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.DataItem;
import com.oktested.entity.ShowDetails;
import com.oktested.favourite.ui.FavouriteFragment;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.showDetail.adapter.ShowVideosAdapter;
import com.oktested.showDetail.presenter.ShowDetailPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.EndlessParentScrollListener;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;

import java.util.ArrayList;

public class ShowDetailActivity extends AppCompatActivity implements ShowDetailView {

    private Context context;
    private ImageView showIV, followShowIV;
    private SpinKitView spinKitView;
    private NestedScrollView nestedScrollView;
    private RelativeLayout showInfoRL, followShowRL;
    private TextView showNameTV, showDescTV, followShowNameTV, allFromNameTV, followTV;
    private ShowDetailPresenter showDetailPresenter;
    private ShowVideosAdapter showVideosAdapter;
    private ArrayList<DataItem> dataItemList = new ArrayList<>();
    private String offset, slug;
    private boolean isFollow, dataLoaded = true, firstTime = true, sw_more, isUserReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        context = this;
        inUi();
        setAdapter();
        callShowVideoApi();

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.MIDDLE_BANNER_AD)) {
            loadMiddleAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.MIDDLE_BANNER_AD));
        }
    }

    private void inUi() {
        showDetailPresenter = new ShowDetailPresenter(context, this);
        slug = getIntent().getExtras().getString("slug");
        sw_more = getIntent().getExtras().getBoolean("sw_more");

        nestedScrollView = findViewById(R.id.nestedScrollView);
        spinKitView = findViewById(R.id.spinKit);
        showInfoRL = findViewById(R.id.showInfoRL);
        followShowRL = findViewById(R.id.followShowRL);

        showIV = findViewById(R.id.showIV);
        followShowIV = findViewById(R.id.followShowIV);

        showNameTV = findViewById(R.id.showNameTV);
        showDescTV = findViewById(R.id.showDescTV);
        allFromNameTV = findViewById(R.id.allFromNameTV);
        followShowNameTV = findViewById(R.id.followShowNameTV);
        followTV = findViewById(R.id.followTV);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());
    }

    private void setShowBasicData(ShowDetails showDetails) {
        if (Helper.isContainValue(showDetails.topic_name)) {
            showIV.setVisibility(View.VISIBLE);
            showInfoRL.setVisibility(View.VISIBLE);

            showNameTV.setText(showDetails.topic_name);
            followShowNameTV.setText("Follow " + showDetails.topic_name);
            allFromNameTV.setText("All from " + showDetails.topic_name);
        }

        if (Helper.isContainValue(showDetails.topic_desc)) {
            showDescTV.setText(showDetails.topic_desc);
        }

        if (Helper.isContainValue(showDetails.onexone_img)) {
            Glide.with(context)
                    .load(showDetails.onexone_img)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(showIV);
        }

        isUserReturn = true;
        setShowFollowIcon(DataHolder.getInstance().getUserDataresponse);
        followShowRL.setOnClickListener(view -> callShowFollowApi());
    }

    private void setShowFollowIcon(GetUserDataresponse response) {
        if (response != null && response.favourite_shows != null && response.favourite_shows.size() > 0) {
            for (int i = 0; i < response.favourite_shows.size(); i++) {
                if (response.favourite_shows.get(i).equalsIgnoreCase(slug)) {
                    isFollow = true;
                    followTV.setText("Following");
                    followTV.setTextColor(Color.parseColor("#717171"));
                    followShowIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_show_follow));
                    followShowRL.setBackground(getResources().getDrawable(R.drawable.following_show_bg));
                    break;
                } else {
                    isFollow = false;
                }
            }
        } else {
            isFollow = false;
            followTV.setText("Follow");
            followTV.setTextColor(getResources().getColor(R.color.colorWhite));
            followShowIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_follow_show));
            followShowRL.setBackground(getResources().getDrawable(R.drawable.follow_show_bg));
        }

        if (!isFollow) {
            followTV.setText("Follow");
            followTV.setTextColor(getResources().getColor(R.color.colorWhite));
            followShowIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_follow_show));
            followShowRL.setBackground(getResources().getDrawable(R.drawable.follow_show_bg));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUserReturn) {
            setShowFollowIcon(DataHolder.getInstance().getUserDataresponse);
        }
    }

    private void callShowFollowApi() {
        if (Helper.isNetworkAvailable(context)) {
            if (isFollow) {
                isFollow = false;
                followTV.setText("Follow");
                followTV.setTextColor(getResources().getColor(R.color.colorWhite));
                followShowIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_follow_show));
                followShowRL.setBackground(getResources().getDrawable(R.drawable.follow_show_bg));
                showDetailPresenter.callUnFavouriteApi(slug);
            } else {
                isFollow = true;
                followTV.setText("Following");
                followTV.setTextColor(Color.parseColor("#717171"));
                followShowIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_show_follow));
                followShowRL.setBackground(getResources().getDrawable(R.drawable.following_show_bg));
                showDetailPresenter.callFavouriteApi(slug);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callShowVideoApi() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            if (sw_more) {
                showDetailPresenter.callScoopWhoopShowVideoApi(slug, "0");
            } else {
                showDetailPresenter.callShowsVideoApi(slug);
            }
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void setAdapter() {
        RecyclerView showVideosRV = findViewById(R.id.showVideosRV);
        showVideosRV.setNestedScrollingEnabled(false);
        showVideosRV.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        showVideosRV.setLayoutManager(mLayoutManager);
        showVideosRV.setItemAnimator(new DefaultItemAnimator());

        showVideosAdapter = new ShowVideosAdapter(context, dataItemList);
        showVideosRV.setAdapter(showVideosAdapter);

        nestedScrollView.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (dataItemList != null && dataItemList.size() >= 9) {
                    if (dataLoaded && Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1")) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            if (sw_more) {
                                showDetailPresenter.callScoopWhoopShowVideoApi(slug, offset);
                            } else {
                                showDetailPresenter.callShowsVideoPagingApi(slug, offset);
                            }
                        }
                    }
                }
            }
        });
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
    protected void onDestroy() {
        showDetailPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse) {
        if (firstTime) {
            firstTime = false;
            if (showsVideoResponse != null && showsVideoResponse.show_details != null) {
                setShowBasicData(showsVideoResponse.show_details);
            }
        }
        if (showsVideoResponse != null && showsVideoResponse.data != null && showsVideoResponse.data.size() > 0) {
            dataItemList.clear();
            dataItemList = showsVideoResponse.data;
            dataLoaded = true;
            offset = showsVideoResponse.next_offset;
            allFromNameTV.setVisibility(View.VISIBLE);
            showVideosAdapter.setShowVideoData(showsVideoResponse.data);
        }
    }

    @Override
    public void setUnFavResponse(UnFavouriteResponse unFavouriteResponse) {
        if (unFavouriteResponse != null) {
            callUserDataApi();
        }
    }

    @Override
    public void setFavResponse(FavouriteResponse favouriteResponse) {
        if (favouriteResponse != null) {
            callUserDataApi();
        }
    }

    private void callUserDataApi() {
        if (Helper.isNetworkAvailable(context)) {
            showDetailPresenter.callUserData();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void getUserData(GetUserResponse getUserResponse) {
        if (getUserResponse != null && getUserResponse.data != null) {
            DataHolder.getInstance().getUserDataresponse = getUserResponse.data;

            if (getUserResponse.data.favourite_shows != null && getUserResponse.data.favourite_shows.size() > 0) {
                FavouriteFragment.callShowsListApi(getUserResponse.data.favourite_shows);
            } else {
                FavouriteFragment.hideShowsView();
            }

            if (getUserResponse.data.follow != null && !(getUserResponse.data.follow.size() > 0) && getUserResponse.data.favourite != null && !(getUserResponse.data.favourite.size() > 0) && getUserResponse.data.favourite_shows != null && !(getUserResponse.data.favourite_shows.size() > 0)) {
                FavouriteFragment.showNoDataView();
            }
        }
    }

    @Override
    public void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse) {
        if (firstTime) {
            firstTime = false;
            if (scoopWhoopShowVideoResponse != null && scoopWhoopShowVideoResponse.show_details != null) {
                setShowBasicData(scoopWhoopShowVideoResponse.show_details);
            }
        }
        if (scoopWhoopShowVideoResponse != null && scoopWhoopShowVideoResponse.data != null && scoopWhoopShowVideoResponse.data.size() > 0) {
            dataItemList.clear();
            dataItemList = scoopWhoopShowVideoResponse.data;
            dataLoaded = true;
            offset = scoopWhoopShowVideoResponse.next_offset;
            allFromNameTV.setVisibility(View.VISIBLE);
            showVideosAdapter.setShowVideoData(scoopWhoopShowVideoResponse.data);
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