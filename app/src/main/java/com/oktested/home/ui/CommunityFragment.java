package com.oktested.home.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.community.model.PollSubmitResponse;
import com.oktested.home.adapter.CommunityAdapter;
import com.oktested.home.model.CommunityPostDataResponse;
import com.oktested.home.model.CommunityPostResponse;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.home.presenter.CommunityPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.CustomSwipeRefreshLayout;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class CommunityFragment extends Fragment implements CommunityView {

    private Context context;
    private CommunityPresenter communityPresenter;
    private CommunityAdapter communityAdapter;
    private int pageNo = 1, position;
    private String selectedOption;
    private boolean dataLoaded = true;
    private SpinKitView spinKitView, bottomProgress;
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private CommunityPostDataResponse dataResponse;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_community, container, false);

        // Trigger GC to clean up prev player.
        // In regular Java vm this is not advised but for Android we need this.
        System.gc();

        communityPresenter = new CommunityPresenter(context, this);
        spinKitView = root_view.findViewById(R.id.spinKit);
        bottomProgress = root_view.findViewById(R.id.bottomProgress);

        RecyclerView communityRV = root_view.findViewById(R.id.communityRV);
        communityRV.setNestedScrollingEnabled(false);
        communityRV.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        communityRV.setLayoutManager(mLayoutManager);
        communityRV.setItemAnimator(new DefaultItemAnimator());

        ArrayList<CommunityPostDataResponse> postDataResponseAL = new ArrayList<>();
        communityAdapter = new CommunityAdapter(context, postDataResponseAL, new CommunityAdapter.PostReaction() {
            @Override
            public void setPostReaction(String type, String postId) {
                if (Helper.isNetworkAvailable(context)) {
                    communityPresenter.sendCommunityPostReaction(postId, type);
                }
            }
        }, new CommunityAdapter.PollSubmit() {
            @Override
            public void callPollSubmit(String option, CommunityPostDataResponse postDataResponse, int pos) {
                if (Helper.isNetworkAvailable(context)) {
                    selectedOption = option;
                    position = pos;
                    dataResponse = postDataResponse;
                    communityPresenter.postPoll(postDataResponse.id, postDataResponse.community_id, option);
                }
            }
        }, new CommunityAdapter.SwipeLayout() {
            @Override
            public void setSwipeLayoutEnable(boolean isEnable) {
                if (isEnable) {
                    swipeRefreshLayout.disableInterceptTouchEvent(false);
                } else {
                    swipeRefreshLayout.disableInterceptTouchEvent(true);
                }
            }
        });
        communityRV.setAdapter(communityAdapter);

        callCommunityPostApi();

        communityRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = mLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            bottomProgress.setVisibility(View.VISIBLE);
                            pageNo = pageNo + 1;
                            callCommunityPostApi();
                        }
                    }
                }
            }
        });

        swipeRefreshLayout = root_view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helper.isNetworkAvailable(context)) {
                    pageNo = 1;
                    swipeRefreshLayout.setRefreshing(false);
                    showLoader();
                    callCommunityPostApi();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(getString(R.string.please_check_internet_connection));
                }
            }
        });

        LocalBroadcastManager.getInstance(context).registerReceiver(pausePlayerReceiver, new IntentFilter(AppConstants.PAUSE_PLAYER));
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, new IntentFilter(AppConstants.COMMUNITY_POST_UPDATE));
        LocalBroadcastManager.getInstance(context).registerReceiver(shareCountReceiver, new IntentFilter(AppConstants.LIST_SHARE_COUNT_UPDATE));

        return root_view;
    }

    private void callCommunityPostApi() {
        if (Helper.isNetworkAvailable(context)) {
            communityPresenter.getCommunityPostApi(String.valueOf(pageNo));
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
    public void onDestroy() {
        if (communityPresenter != null) {
            communityPresenter.onDestroyedView();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(pausePlayerReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(shareCountReceiver);
            communityAdapter.releaseExoPlayer();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        pausePlayer();
    }

    public void pausePlayer() {
        if (communityAdapter != null) {
            communityAdapter.pauseExoPlayer();
        }
    }

    @Override
    public void setCommunityPostResponse(CommunityPostResponse communityPostResponse) {
        bottomProgress.setVisibility(View.GONE);
        if (communityPostResponse != null && communityPostResponse.data != null && communityPostResponse.data.size() > 0) {
            dataLoaded = true;
            if (pageNo == 1) {
                communityAdapter.setFreshData(communityPostResponse.data);
            } else {
                communityAdapter.setMorePostsData(communityPostResponse.data);
            }
        }
    }

    @Override
    public void setPostReactionResponse(PostReactionResponse postReactionResponse) {

    }

    @Override
    public void setPostShareResponse(PostShareResponse postShareResponse) {
        if (postShareResponse != null && Helper.isContainValue(postShareResponse.status) && postShareResponse.status.equalsIgnoreCase("success")) {
            communityAdapter.updateShareCount(position);
        }
    }

    @Override
    public void setPollSubmitResponse(PollSubmitResponse pollSubmitResponse) {
        if (pollSubmitResponse != null && pollSubmitResponse.data != null && pollSubmitResponse.data.choices != null && pollSubmitResponse.data.choices.size() > 0 && Helper.isContainValue(pollSubmitResponse.status) && pollSubmitResponse.status.equalsIgnoreCase("success")) {
            if (Helper.isContainValue(pollSubmitResponse.message)) {
                showMessage(pollSubmitResponse.message);
            }

            dataResponse.is_polled = true;
            dataResponse.selected_poll_option = selectedOption;
            dataResponse.choices.clear();
            dataResponse.choices.addAll(pollSubmitResponse.data.choices);
            communityAdapter.updateItemChanges(dataResponse, position);
        }
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                CommunityPostDataResponse postDataResponse = (CommunityPostDataResponse) intent.getSerializableExtra("postData");
                int position = intent.getExtras().getInt("position");
                communityAdapter.updateItemChanges(postDataResponse, position);
            }
        }
    };

    private BroadcastReceiver shareCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String postId = intent.getExtras().getString("postId");
                String appName = intent.getExtras().getString("appName");
                String communityId = intent.getExtras().getString("communityId");
                if (Helper.isNetworkAvailable(context)) {
                    position = intent.getExtras().getInt("position");
                    communityPresenter.sendCommunityPostShareCount(postId, appName, communityId);
                }
            }
        }
    };

    private BroadcastReceiver pausePlayerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pausePlayer();
        }
    };
}