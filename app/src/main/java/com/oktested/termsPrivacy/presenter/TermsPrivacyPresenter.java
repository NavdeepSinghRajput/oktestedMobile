package com.oktested.termsPrivacy.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.termsPrivacy.model.CommunityGuidelineResponse;
import com.oktested.termsPrivacy.model.PrivacyPolicyResponse;
import com.oktested.termsPrivacy.model.TermsConditionResponse;
import com.oktested.termsPrivacy.ui.TermsPrivacyView;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class TermsPrivacyPresenter extends NetworkHandler {

    private TermsPrivacyView termsPrivacyView;

    public TermsPrivacyPresenter(Context context, TermsPrivacyView termsPrivacyView) {
        super(context, termsPrivacyView);
        this.termsPrivacyView = termsPrivacyView;
    }

    public void getPrivacyPolicy() {
        NetworkManager.getApi().getPrivacyPolicy().enqueue(this);
    }

    public void getTermsCondition() {
        NetworkManager.getApi().getTermsCondition().enqueue(this);
    }

    public void getCommunityGuidelines() {
        NetworkManager.getApi().getCommunityGuideline().enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (termsPrivacyView != null) {
            termsPrivacyView.hideLoader();
            if (response instanceof PrivacyPolicyResponse) {
                PrivacyPolicyResponse privacyPolicyResponse = (PrivacyPolicyResponse) response;
                termsPrivacyView.showPrivacyResponse(privacyPolicyResponse);
            } else if (response instanceof TermsConditionResponse) {
                TermsConditionResponse termsConditionResponse = (TermsConditionResponse) response;
                termsPrivacyView.showTermsResponse(termsConditionResponse);
            } else if (response instanceof CommunityGuidelineResponse) {
                CommunityGuidelineResponse communityGuidelineResponse = (CommunityGuidelineResponse) response;
                termsPrivacyView.showCommunityGuidelineResponse(communityGuidelineResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (termsPrivacyView != null) {
            termsPrivacyView.hideLoader();
            termsPrivacyView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (termsPrivacyView != null) {
            termsPrivacyView.hideLoader();
            if (Helper.isContainValue(message)) {
                termsPrivacyView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    public void onDestroyedView() {
        termsPrivacyView = null;
    }
}