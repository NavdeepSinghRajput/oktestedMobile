package com.oktested.userDetail.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.userDetail.model.UpdateUserResponse;

public interface UserDetailView extends MasterView {
    void setUpdateData(UpdateUserResponse updateUserResponse);
}