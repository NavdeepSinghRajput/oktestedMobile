package com.oktested.termsPrivacy.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.termsPrivacy.model.CommunityGuidelineResponse;
import com.oktested.termsPrivacy.model.PrivacyPolicyResponse;
import com.oktested.termsPrivacy.model.TermsConditionResponse;

public interface TermsPrivacyView extends MasterView {

    void showPrivacyResponse(PrivacyPolicyResponse privacyPolicyResponse);

    void showTermsResponse(TermsConditionResponse termsConditionResponse);

    void showCommunityGuidelineResponse(CommunityGuidelineResponse communityGuidelineResponse);
}