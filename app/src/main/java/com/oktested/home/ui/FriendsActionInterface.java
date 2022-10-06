package com.oktested.home.ui;


import com.oktested.roomDatabase.model.ContactModel;

public interface FriendsActionInterface {

    void callAddRequestApi(int position, ContactModel contactModel);

    void callInviteApi(String contactNumber);

    void callFriendAcceptApi(ContactModel contactModel, int position);
}
