package com.oktested.termsPrivacy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.termsPrivacy.model.CommunityGuidelineResponse;
import com.oktested.termsPrivacy.model.PrivacyPolicyResponse;
import com.oktested.termsPrivacy.model.TermsConditionResponse;
import com.oktested.termsPrivacy.presenter.TermsPrivacyPresenter;
import com.oktested.utils.Helper;

public class TermsPrivacyActivity extends AppCompatActivity implements TermsPrivacyView {

    private Context context;
    private SpinKitView spinKitView;
    private TextView contentTV;
    private TermsPrivacyPresenter termsPrivacyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy);
        context = this;

        inUi();
    }

    private void inUi() {
        termsPrivacyPresenter = new TermsPrivacyPresenter(this, TermsPrivacyActivity.this);
        spinKitView = findViewById(R.id.spinKit);
        TextView headerTV = findViewById(R.id.headerTV);
        contentTV = findViewById(R.id.contentTV);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        String from = getIntent().getExtras().getString("from");
        if (from.equalsIgnoreCase("terms")) {
            headerTV.setText("Terms & Conditions");
            if (Helper.isNetworkAvailable(context)) {
                showLoader();
                termsPrivacyPresenter.getTermsCondition();
            } else {
                showMessage(getString(R.string.please_check_internet_connection));
            }
        } else if (from.equalsIgnoreCase("privacy")) {
            headerTV.setText("Privacy Policy");
            if (Helper.isNetworkAvailable(context)) {
                showLoader();
                termsPrivacyPresenter.getPrivacyPolicy();
            } else {
                showMessage(getString(R.string.please_check_internet_connection));
            }
        } else {
            headerTV.setText("Community Guidelines");
            if (Helper.isNetworkAvailable(context)) {
                showLoader();
                termsPrivacyPresenter.getCommunityGuidelines();
            } else {
                showMessage(getString(R.string.please_check_internet_connection));
            }
        }
    }

    @Override
    public void showLoader() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPrivacyResponse(PrivacyPolicyResponse privacyPolicyResponse) {
        if (privacyPolicyResponse != null && privacyPolicyResponse.data != null && Helper.isContainValue(privacyPolicyResponse.data.content)) {
            contentTV.setText(privacyPolicyResponse.data.content);
        }
    }

    @Override
    public void showTermsResponse(TermsConditionResponse termsConditionResponse) {
        if (termsConditionResponse != null && termsConditionResponse.data != null && Helper.isContainValue(termsConditionResponse.data.content)) {
            contentTV.setText(termsConditionResponse.data.content);
        }
    }

    @Override
    public void showCommunityGuidelineResponse(CommunityGuidelineResponse communityGuidelineResponse) {
        if (communityGuidelineResponse != null && communityGuidelineResponse.data != null && Helper.isContainValue(communityGuidelineResponse.data.content)) {
            contentTV.setText(communityGuidelineResponse.data.content);
        }
    }

    @Override
    public void onDestroy() {
        termsPrivacyPresenter.onDestroyedView();
        super.onDestroy();
    }
}