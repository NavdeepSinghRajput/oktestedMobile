package com.oktested.reportProblem.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.oktested.R;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.reportProblem.presenter.ReportProblemPresenter;
import com.oktested.utils.Helper;

public class ReportProblemActivity extends AppCompatActivity implements ReportProblemView {

    private Context context;
    private EditText reportIssueET;
    private TextView submitTV;
    private SpinKitView spinKitView;
    private ReportProblemPresenter reportProblemPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        context = this;
        inUi();
    }

    private void inUi() {
        reportProblemPresenter = new ReportProblemPresenter(context, this);

        spinKitView = findViewById(R.id.spinKit);
        reportIssueET = findViewById(R.id.reportIssueET);
        submitTV = findViewById(R.id.submitTV);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());

        submitTV.setOnClickListener(view -> {
            if (!Helper.isContainValue(reportIssueET.getText().toString().trim())) {
                showMessage("Please enter some text.");
            } else if (reportIssueET.getText().toString().trim().length() > 500) {
                showMessage("Max limit reached.");
            } else {
                postReportProblem();
            }
        });
    }

    private void postReportProblem() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            reportProblemPresenter.callReportApi(reportIssueET.getText().toString().trim());
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        reportProblemPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void showResponse(ReportProblemResponse reportProblemResponse) {
        if (reportProblemResponse != null) {
            if (Helper.isContainValue(reportProblemResponse.message)) {
                showMessage(reportProblemResponse.message);
            }
            finish();
        }
    }
}