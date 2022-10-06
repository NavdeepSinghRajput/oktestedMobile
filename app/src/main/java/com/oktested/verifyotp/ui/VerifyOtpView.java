package com.oktested.verifyotp.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.verifyotp.model.ResendOtpResponse;
import com.oktested.verifyotp.model.VerifyOtpResponse;

public interface VerifyOtpView extends MasterView {
    void verifyOtpResponse(VerifyOtpResponse response);

    void resendOtpResponse(ResendOtpResponse response);
}