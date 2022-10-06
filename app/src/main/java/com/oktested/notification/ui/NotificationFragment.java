package com.oktested.notification.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.RejectFriendResponse;
import com.oktested.notification.adapter.NotificationAdapter;
import com.oktested.notification.model.NotifcationModelItem;
import com.oktested.notification.model.NotificationsResponse;
import com.oktested.notification.presenter.NotificationPresenter;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class NotificationFragment extends Fragment implements NotificationView, AcceptRejectRequestInterface {

    private NotificationPresenter notificationPresenter;
    private SpinKitView spinKitView;
    private NotificationAdapter notificationAdapter;
    private LinearLayout notificationLL;
    private TextView noDataLL;
    private RecyclerView notificationRV;
    private ArrayList<NotifcationModelItem> notificationModelAL = new ArrayList<>();
    private int page = 1;
    private boolean dataLoaded = true;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean firstTym = true;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_notifcation, container, false);
        inUi(root_view);
        return root_view;
    }

    private void inUi(View root_view) {
        setUserVisibleHint(false);
        notificationPresenter = new NotificationPresenter(context, this);
        spinKitView = root_view.findViewById(R.id.spinKit);
        notificationRV = (RecyclerView) root_view.findViewById(R.id.notificationRV);
        notificationLL = (LinearLayout) root_view.findViewById(R.id.notifcationLL);
        noDataLL = (TextView) root_view.findViewById(R.id.noDataLL);
        NestedScrollView nestedScrollView = root_view.findViewById(R.id.nestedScrollView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        notificationRV.setNestedScrollingEnabled(false);
        notificationRV.setHasFixedSize(true);
        notificationRV.setLayoutManager(linearLayoutManager);
        notificationRV.setItemAnimator(new DefaultItemAnimator());
        notificationAdapter = new NotificationAdapter(context, this, notificationModelAL);
        notificationRV.setAdapter(notificationAdapter);

       /* nestedScrollView.setOnScrollChangeListener(new EndlessParentScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount) {
                Log.e("scroling",pageNumber+"  dsd  "+totalItemsCount);
                if (notificationModelAL != null && notificationModelAL.size() > 9) {
                    if (dataLoaded && page != -1) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            notificationPresenter.callNotificationApi(page);
                        }
                    }
                }
            }
        });*/
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    final int visibleThreshold = 4;
                    int lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = linearLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded && page != -1) {
                        if (Helper.isNetworkAvailable(context)) {
                            dataLoaded = false;
                            notificationPresenter.callNotificationApi(page);
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
                    swipeRefreshLayout.setRefreshing(false);
                    notificationModelAL.clear();
                    notificationAdapter.clearAll();
                    page = 1;
                    notificationPresenter.callNotificationApi(page);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(getString(R.string.please_check_internet_connection));
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
    public void setNotificationResponse(NotificationsResponse notificationsResponse) {
        if (notificationsResponse != null && notificationsResponse.data != null && notificationsResponse.data.size() > 0) {
            dataLoaded = true;
            page = notificationsResponse.next_page;
            notificationAdapter.notifyItem(notificationsResponse.data);
            noDataLL.setVisibility(View.GONE);
            notificationLL.setVisibility(View.VISIBLE);
            notificationRV.setVisibility(View.VISIBLE);
            if (firstTym) {
                ((DashboardActivity) context).removeBadge();
            }
        } else {
            noDataLL.setVisibility(View.VISIBLE);
            notificationRV.setVisibility(View.GONE);
            notificationLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (firstTym && notificationPresenter != null) {
                notificationPresenter.callNotificationApi(page);
                firstTym = false;
                showLoader();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void setAcceptFriendResponse(AcceptFriendResponse acceptFriendResponse, int position) {
        if (acceptFriendResponse != null && acceptFriendResponse.status.equalsIgnoreCase("1")) {
            notificationAdapter.notifyItemPostion(position, acceptFriendResponse.data);
            showMessage(acceptFriendResponse.message);
            Intent intent = new Intent();
            intent.setAction(getString(R.string.app_name) + "user.refresh");
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void setRejectFriendResponse(RejectFriendResponse rejectFriendResponse, int position) {
        if (rejectFriendResponse.status.equalsIgnoreCase("1")) {
            notificationAdapter.removeNotify(position);

        }
        showMessage(rejectFriendResponse.message);
    }

    @Override
    public void callAcceptRequestApi(NotifcationModelItem.DataMessge dataMessge, int postion) {
        notificationPresenter.callAcceptFriendRequestApi(dataMessge.request_send_by, dataMessge.contact, dataMessge.connect_type, postion);
    }

    @Override
    public void callRejectRequestApi(NotifcationModelItem.DataMessge dataMessge, int position) {
        notificationPresenter.callRejectFriendRequestApi(dataMessge.request_send_by, dataMessge.contact, dataMessge.connect_type, position);
    }
}