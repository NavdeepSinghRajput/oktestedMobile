package com.oktested.notification.ui;


import com.oktested.notification.model.NotifcationModelItem;

public interface AcceptRejectRequestInterface {

    void callAcceptRequestApi(NotifcationModelItem.DataMessge dataMessge, int position);

    void callRejectRequestApi(NotifcationModelItem.DataMessge dataMessge, int position);

}
