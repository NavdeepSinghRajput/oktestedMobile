package com.oktested.login.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.login.model.CreateUserResponse;
import com.oktested.login.model.SocialLoginResponse;

public interface LoginView extends MasterView {

    void onResponse(CreateUserResponse createUserResponse);

    void socialLoginResponse(SocialLoginResponse socialLoginResponse);
}