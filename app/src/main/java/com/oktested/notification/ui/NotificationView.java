package com.oktested.notification.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.RejectFriendResponse;
import com.oktested.notification.model.NotificationsResponse;

public interface NotificationView extends MasterView {

    void setNotificationResponse(NotificationsResponse notificationsResponse);

    void setAcceptFriendResponse(AcceptFriendResponse notifcationModelItem, int position);

    void setRejectFriendResponse(RejectFriendResponse rejectFriendResponse,int position);
}
