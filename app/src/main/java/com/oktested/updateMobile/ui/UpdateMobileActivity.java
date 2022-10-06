package com.oktested.updateMobile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.updateMobile.model.UpdateMobileNumberResponse;
import com.oktested.updateMobile.presenter.UpdateMobilePresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

public class UpdateMobileActivity extends AppCompatActivity implements UpdateMobileView, View.OnClickListener {

    private EditText mobileET;
    private Button continueButton;
    private TextView tvErrorTV, skipTV;
    private SpinKitView spinKitView;
    private Context context;
    private UpdateMobilePresenter updateMobilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mobile);
        inUi();
    }

    private void inUi() {
        context = this;
        updateMobilePresenter = new UpdateMobilePresenter(context, this);

        mobileET = findViewById(R.id.mobileET);
        tvErrorTV = findViewById(R.id.tvErrorTV);
        skipTV = findViewById(R.id.skipTV);
        continueButton = findViewById(R.id.continueButton);
        spinKitView = findViewById(R.id.spinKit);

        skipTV.setOnClickListener(this);
        continueButton.setOnClickListener(this);
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
        Snackbar.make(continueButton, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueButton:
                validate(mobileET.getText().toString().trim());
                break;

            case R.id.skipTV:
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_MOBILE_DETAIL, "detailSaved");
                startActivity(new Intent(context, DashboardActivity.class));
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        updateMobilePresenter.onDestroyedView();
        super.onDestroy();
    }

    private void validate(String mobile) {
        if (!Helper.isContainValue(mobile)) {
            tvErrorTV.setVisibility(View.VISIBLE);
            tvErrorTV.setText(getString(R.string.mobile_error));
        } else if (mobile.length() != 10) {
            tvErrorTV.setVisibility(View.VISIBLE);
            tvErrorTV.setText(getString(R.string.mobile_not_valid));
        } else if (!Helper.isNetworkAvailable(context)) {
            showMessage(getString(R.string.please_check_internet_connection));
        } else {
            showLoader();
            tvErrorTV.setVisibility(View.GONE);
            updateMobilePresenter.updateMobileNumberApi(mobile);
        }
    }

    @Override
    public void setUpdateMobileNumberResponse(UpdateMobileNumberResponse updateMobileNumberResponse) {
        hideLoader();
        if (updateMobileNumberResponse != null) {
            if (Helper.isContainValue(updateMobileNumberResponse.status) && updateMobileNumberResponse.status.equalsIgnoreCase("success")) {
                String mobile = mobileET.getText().toString();
                startActivity(new Intent(context, VerifyMobileActivity.class).putExtra("mobile", mobile));
            } else {
                if (Helper.isContainValue(updateMobileNumberResponse.message)) {
                    showMessage(updateMobileNumberResponse.message);
                }
            }
        }
    }
}