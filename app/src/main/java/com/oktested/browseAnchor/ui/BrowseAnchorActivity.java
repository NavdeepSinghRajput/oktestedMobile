package com.oktested.browseAnchor.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.browseAnchor.adapter.BrowseAnchorAdapter;
import com.oktested.browseAnchor.presenter.BrowseAnchorPresenter;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.home.model.AnchorsListModel;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;

import java.util.ArrayList;

public class BrowseAnchorActivity extends AppCompatActivity implements BrowseAnchorView {

    private SpinKitView spinKitView;
    private Context context;
    private BrowseAnchorPresenter browseAnchorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_anchor);
        context = this;

        browseAnchorPresenter = new BrowseAnchorPresenter(context, this);
        spinKitView = findViewById(R.id.spinKit);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        if (DataHolder.getInstance().anchorList != null && DataHolder.getInstance().anchorList.size() > 0) {
            setAdapter(DataHolder.getInstance().anchorList);
        } else {
            callAnchorsApiData();
        }

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.BOTTOM_BANNER_AD));
        }
    }

    private void callAnchorsApiData() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            browseAnchorPresenter.callAnchorsApi();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void setAdapter(ArrayList<AnchorsListModel> anchorList) {
        RecyclerView anchorsRV = findViewById(R.id.anchorsRV);
        anchorsRV.setHasFixedSize(true);
        anchorsRV.setLayoutManager(new GridLayoutManager(context, 3));
        anchorsRV.setItemAnimator(new DefaultItemAnimator());

        BrowseAnchorAdapter browseAnchorAdapter = new BrowseAnchorAdapter(context, anchorList);
        anchorsRV.setAdapter(browseAnchorAdapter);
    }

    @Override
    public void setAnchorsResponse(AnchorsResponse anchorsResponse) {
        if (anchorsResponse != null && anchorsResponse.data != null && anchorsResponse.data.size() > 0) {
            DataHolder.getInstance().anchorList = anchorsResponse.data;
            setAdapter(anchorsResponse.data);
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
        Toast.makeText(BrowseAnchorActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        browseAnchorPresenter.onDestroyedView();
        super.onDestroy();
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout bottomAdView = findViewById(R.id.bottomAdView);
            MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }
}