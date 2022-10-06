package com.oktested.notification.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.home.adapter.QuizAdapter;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.RejectFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.notification.adapter.NotificationAdapter;
import com.oktested.notification.model.NotifcationModelItem;
import com.oktested.notification.model.NotificationsResponse;
import com.oktested.notification.presenter.NotificationPresenter;
import com.oktested.notification.ui.NotificationView;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements NotificationView,AcceptRejectRequestInterface {

    NotificationPresenter notificationPresenter;
    SpinKitView spinKitView;
    NotificationAdapter notificationAdapter;
    RecyclerView notificationRV;
    int page=1;
    boolean dataLoaded=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationPresenter = new NotificationPresenter(getBaseContext(), this);
        spinKitView = findViewById(R.id.spinKit);
        notificationRV = (RecyclerView) findViewById(R.id.notificationRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        notificationRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 4;
                    int lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = linearLayoutManager.getItemCount();
                    if (currentTotalCount <= lastItem + visibleThreshold && dataLoaded && page!=-1) {
                        if (Helper.isNetworkAvailable(getBaseContext())) {
                            dataLoaded = false;
                            notificationPresenter.callNotificationApi(page);
                        }
                    }
                }
            }
        });
        notificationRV.setNestedScrollingEnabled(false);
        notificationRV.setHasFixedSize(true);
        notificationRV.setLayoutManager(linearLayoutManager);
        notificationRV.setItemAnimator(new DefaultItemAnimator());
        ArrayList<NotifcationModelItem> notifcationModels = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getBaseContext(),this,notifcationModels);
        notificationRV.setAdapter(notificationAdapter);
        notificationPresenter.callNotificationApi(page);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

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
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNotificationResponse(NotificationsResponse notificationsResponse) {
        if (notificationsResponse != null && notificationsResponse.data != null && notificationsResponse.data.size() > 0) {
            dataLoaded = true;
            page = notificationsResponse.next_page;
            notificationAdapter.notifyItem(notificationsResponse.data);

        }
    }

    @Override
    public void setAcceptFriendResponse(AcceptFriendResponse acceptFriendResponse, int position) {
        notificationAdapter.notifyItemPostion(position,acceptFriendResponse.data);
        showMessage(acceptFriendResponse.message);
    }

    @Override
    public void setRejectFriendResponse(RejectFriendResponse rejectFriendResponse, int position) {
        if(rejectFriendResponse.status.equalsIgnoreCase("1")){
            notificationAdapter.removeNotify(position);
        }
        showMessage(rejectFriendResponse.message);
    }

    @Override
    public void callAcceptRequestApi(NotifcationModelItem.DataMessge dataMessge, int postion) {
        notificationPresenter.callAcceptFriendRequestApi(dataMessge.request_send_by,dataMessge.contact,dataMessge.connect_type,postion);
    }

    @Override
    public void callRejectRequestApi(NotifcationModelItem.DataMessge dataMessge,int position) {
        notificationPresenter.callRejectFriendRequestApi(dataMessge.request_send_by,dataMessge.contact,dataMessge.connect_type,position);
    }
}