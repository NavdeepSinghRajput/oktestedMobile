package com.oktested.browseShow.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.browseShow.adapter.BrowseShowAdapter;
import com.oktested.browseShow.presenter.BrowseShowsPresenter;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ShowsListModel;
import com.oktested.home.model.ShowsResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;

import java.util.ArrayList;

public class BrowseShowActivity extends AppCompatActivity implements BrowseShowsView {

    private SpinKitView spinKitView;
    private Context context;
    private BrowseShowsPresenter browseShowsPresenter;
    private RecyclerView showsRV;
    private GridLayoutManager mLayoutManager;
    private BrowseShowAdapter browseShowAdapter;
    private boolean dataLoaded = true, firstTime = true;
    private String offset, screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_show);
        context = this;

        browseShowsPresenter = new BrowseShowsPresenter(context, this);
        spinKitView = findViewById(R.id.spinKit);
        TextView headingTV = findViewById(R.id.headingTV);
        headingTV.setText(getIntent().getExtras().getString("heading"));

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        screenName = getIntent().getExtras().getString("screenName");
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("showModel")) {
            offset = getIntent().getExtras().getString("offset");
            setAdapter(getIntent().getExtras().getParcelableArrayList("showModel"));

            // For fix the issue of paging api not call due to less content on screen
            if (Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1") && firstTime) {
                firstTime = false;
                if (Helper.isNetworkAvailable(context)) {
                    if (screenName.equalsIgnoreCase(AppConstants.SECTION_SHOWS_LAYOUT)) {
                        browseShowsPresenter.callShowsApi(offset);
                    } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_LAYOUT)) {
                        browseShowsPresenter.callScoopWhoopShowsApi(offset);
                    }
                }
            }
        } else {
            ArrayList<ShowsListModel> showsListModel = new ArrayList<>();
            setAdapter(showsListModel);
            callShowsApiData();
        }

        showsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            if (screenName.equalsIgnoreCase(AppConstants.SECTION_SHOWS_LAYOUT)) {
                                browseShowsPresenter.callShowsApi(offset);
                            } else if (screenName.equalsIgnoreCase(AppConstants.SECTION_SW_SHOWS_LAYOUT)) {
                                browseShowsPresenter.callScoopWhoopShowsApi(offset);
                            }
                        }
                    }
                }
            }
        });

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
    }

    private void callShowsApiData() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            browseShowsPresenter.callShowsApi("0");
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void setAdapter(ArrayList<ShowsListModel> showsListModel) {
        showsRV = findViewById(R.id.showsRV);
        showsRV.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(context, 3);
        showsRV.setLayoutManager(mLayoutManager);
        showsRV.setItemAnimator(new DefaultItemAnimator());

        browseShowAdapter = new BrowseShowAdapter(context, showsListModel);
        showsRV.setAdapter(browseShowAdapter);
    }

    @Override
    public void setShowsResponse(ShowsResponse showsResponse) {
        if (showsResponse != null && showsResponse.data != null && showsResponse.data.size() > 0) {
            dataLoaded = true;
            offset = showsResponse.next_offset;
            browseShowAdapter.setMoreShowsData(showsResponse.data);

            // For fix the issue of paging api not call due to less content on screen
            if (Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1") && firstTime) {
                firstTime = false;
                if (Helper.isNetworkAvailable(context)) {
                    browseShowsPresenter.callShowsApi(offset);
                }
            }
        }
    }

    @Override
    public void setScoopWhoopShowsResponse(ScoopWhoopShowsResponse scoopWhoopShowsResponse) {
        if (scoopWhoopShowsResponse != null && scoopWhoopShowsResponse.data != null && scoopWhoopShowsResponse.data.size() > 0) {
            dataLoaded = true;
            offset = scoopWhoopShowsResponse.next_offset;
            browseShowAdapter.setMoreShowsData(scoopWhoopShowsResponse.data);

            // For fix the issue of paging api not call due to less content on screen
            if (Helper.isContainValue(offset) && !offset.equalsIgnoreCase("-1") && firstTime) {
                firstTime = false;
                if (Helper.isNetworkAvailable(context)) {
                    browseShowsPresenter.callScoopWhoopShowsApi(offset);
                }
            }
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
    protected void onDestroy() {
        browseShowsPresenter.onDestroyedView();
        super.onDestroy();
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout bottomAdView = findViewById(R.id.bottomAdView);
            MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }
}