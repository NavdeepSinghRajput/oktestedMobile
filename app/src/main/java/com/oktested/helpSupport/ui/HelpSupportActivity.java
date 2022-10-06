package com.oktested.helpSupport.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.helpSupport.adapter.HelpSupportAdapter;
import com.oktested.helpSupport.model.HelpModel;
import com.oktested.helpSupport.model.HelpResponse;
import com.oktested.helpSupport.presenter.HelpSupportPresenter;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class HelpSupportActivity extends AppCompatActivity implements HelpSupportView {

    private Context context;
    private SpinKitView spinKitView;
    private HelpSupportPresenter helpSupportPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);
        context = this;
        inUi();

        callHelpApiData();
    }

    private void inUi() {
        helpSupportPresenter = new HelpSupportPresenter(context, this);
        spinKitView = findViewById(R.id.spinKit);

        LinearLayout backLL = findViewById(R.id.backLL);
        backLL.setOnClickListener(view -> finish());
    }

    private void callHelpApiData() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            helpSupportPresenter.callHelpApi();
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
        helpSupportPresenter.onDestroyedView();
        super.onDestroy();
    }

    @Override
    public void setHelpResponse(HelpResponse helpResponse) {
        if (helpResponse != null && helpResponse.data != null && helpResponse.data.size() > 0) {
            setAdapter(helpResponse.data);
        }
    }

    private void setAdapter(ArrayList<HelpModel> helpData) {
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setDividerHeight(0);
        HelpSupportAdapter adapter = new HelpSupportAdapter(context, helpData);
        expandableListView.setAdapter(adapter);
    }
}