package com.oktested.updateMobile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.updateMobile.model.VerifyMobileNumberResponse;
import com.oktested.updateMobile.presenter.VerifyMobilePresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.utils.SMSReceiver;
import com.oktested.verifyotp.model.ResendOtpResponse;

public class VerifyMobileActivity extends AppCompatActivity implements VerifyMobileView, View.OnClickListener {

    private VerifyMobilePresenter verifyMobilePresenter;
    private TextView resendTV, verifyTV;
    private SpinKitView spinKit;
    private EditText et_Otp1, et_Otp2, et_Otp3, et_Otp4;
    private String mobile;
    private SMSReceiver smsReceiver;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        context = this;
        inUi();

        if (getIntent() != null) {
            mobile = getIntent().getStringExtra("mobile");
        }

        startSMSListenerReceiver();
        startSMSListener();
    }

    private void inUi() {
        verifyMobilePresenter = new VerifyMobilePresenter(context, this);
        resendTV = findViewById(R.id.resendTV);
        verifyTV = findViewById(R.id.verifyTV);
        spinKit = findViewById(R.id.spinKit);

        et_Otp1 = findViewById(R.id.et_Otp1);
        et_Otp2 = findViewById(R.id.et_Otp2);
        et_Otp3 = findViewById(R.id.et_Otp3);
        et_Otp4 = findViewById(R.id.et_Otp4);

        resendTV.setOnClickListener(this);
        verifyTV.setOnClickListener(this);

        et_Otp1.addTextChangedListener(new GenericTextWatcher(et_Otp1));
        et_Otp2.addTextChangedListener(new GenericTextWatcher(et_Otp2));
        et_Otp3.addTextChangedListener(new GenericTextWatcher(et_Otp3));
        et_Otp4.addTextChangedListener(new GenericTextWatcher(et_Otp4));
    }

    private void startSMSListenerReceiver() {
        smsReceiver = new SMSReceiver();
        smsReceiver.setOTPListener(new SMSReceiver.OTPReceiveListener() {
            @Override
            public void onOTPReceived(String otp) {
                Log.d("SmsRetriever", "OTP Received: " + otp);
                if (Helper.isContainValue(otp)) {
                    et_Otp1.setText(String.valueOf(otp.charAt(0)));
                    et_Otp2.setText(String.valueOf(otp.charAt(1)));
                    et_Otp3.setText(String.valueOf(otp.charAt(2)));
                    et_Otp4.setText(String.valueOf(otp.charAt(3)));
                    if (Helper.isNetworkAvailable(context)) {
                        showLoader();
                        verifyMobilePresenter.verifyMobileNumberApi(mobile, otp);
                    } else {
                        showMessage(getString(R.string.please_check_internet_connection));
                    }
                }
            }

            @Override
            public void onOTPTimeOut() {
                Log.d("SmsRetriever", "OTP Time out");
            }

            @Override
            public void onOTPReceivedError(String error) {
                Log.d("SmsRetriever", error);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        this.registerReceiver(smsReceiver, intentFilter);
    }

    private void startSMSListener() {
        try {
            SmsRetrieverClient client = SmsRetriever.getClient(this);
            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (smsReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        verifyMobilePresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verifyTV:
                validate();
                break;

            case R.id.resendTV:
                if (Helper.isNetworkAvailable(context)) {
                    showLoader();
                    verifyMobilePresenter.callResendOtpApi("+91" + mobile);
                    startSMSListener();
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void verifyMobileNumberResponse(VerifyMobileNumberResponse verifyMobileNumberResponse) {
        hideLoader();
        if (verifyMobileNumberResponse != null && Helper.isContainValue(verifyMobileNumberResponse.status) && verifyMobileNumberResponse.status.equalsIgnoreCase("success")) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_MOBILE_DETAIL, "detailSaved");
            startActivity(new Intent(context, DashboardActivity.class));
            finishAffinity();
        }
    }

    @Override
    public void resendOtpResponse(ResendOtpResponse response) {
        hideLoader();
        if (!response.status.equalsIgnoreCase("fail")) {
            if (Helper.isContainValue(response.message)) {
                showMessage(response.message);
            }
        } else {
            showMessage(response.message);
            if (Helper.isContainValue(response.code)) {
                if (response.code.equalsIgnoreCase("max_limit_reached_for_this_otp_verification")) {
                    resendTV.setVisibility(View.INVISIBLE);
                    verifyTV.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void showLoader() {
        spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKit.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(verifyTV, message, Snackbar.LENGTH_SHORT).show();
    }

    private void validate() {
        String otp1 = et_Otp1.getText().toString().trim();
        String otp2 = et_Otp2.getText().toString().trim();
        String otp3 = et_Otp3.getText().toString().trim();
        String otp4 = et_Otp4.getText().toString().trim();
        String validOtp = otp1 + otp2 + otp3 + otp4;
        if (!Helper.isContainValue(validOtp)) {
            showMessage("Please enter OTP.");
        } else if (validOtp.length() < 4) {
            showMessage("Please enter valid OTP.");
        } else if (!Helper.isNetworkAvailable(context)) {
            showMessage(getString(R.string.please_check_internet_connection));
        } else {
            showLoader();
            verifyMobilePresenter.verifyMobileNumberApi(mobile, validOtp);
        }
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.et_Otp1:
                    if (text.length() == 1)
                        et_Otp2.requestFocus();
                    break;
                case R.id.et_Otp2:
                    if (text.length() == 1)
                        et_Otp3.requestFocus();
                    else if (text.length() == 0)
                        et_Otp1.requestFocus();
                    break;
                case R.id.et_Otp3:
                    if (text.length() == 1)
                        et_Otp4.requestFocus();
                    else if (text.length() == 0)
                        et_Otp2.requestFocus();
                    break;
                case R.id.et_Otp4:
                    if (text.length() == 1)
                        Helper.hideKeyboard(VerifyMobileActivity.this);
                    else if (text.length() == 0)
                        et_Otp3.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }
    }
}