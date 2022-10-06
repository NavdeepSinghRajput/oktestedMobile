package com.oktested.updateMobile.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.updateMobile.model.VerifyMobileNumberResponse;
import com.oktested.verifyotp.model.ResendOtpResponse;

public interface VerifyMobileView extends MasterView {
    void verifyMobileNumberResponse(VerifyMobileNumberResponse verifyMobileNumberResponse);

    void resendOtpResponse(ResendOtpResponse response);
}