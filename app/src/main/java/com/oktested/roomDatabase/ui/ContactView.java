package com.oktested.roomDatabase.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.NotifyInviteFriendResponse;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.userDetail.model.UpdateUserResponse;

import java.util.List;

public interface ContactView extends MasterView {

    void getContactData(List<ContactModel> contactModels);

}