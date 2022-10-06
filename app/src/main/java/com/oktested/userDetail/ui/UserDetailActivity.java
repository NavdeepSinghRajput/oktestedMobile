package com.oktested.userDetail.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.updateMobile.ui.UpdateMobileActivity;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.userDetail.presenter.UserDetailPresenter;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener, UserDetailView {

    private Context context;
    private RelativeLayout dobRL, genderRL, maleRL, femaleRL, otherRL;
    private LinearLayout dobLL;
    private ImageView maleIV, femaleIV, otherIV;
    private Button saveButton, nextButton;
    private TextView dobTV;
    private EditText nameET;
    private Spinner daySpinner, monthSpinner, yearSpinner;
    private String[] dayMode = {"Day", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private String[] monthMode = {"Month", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] yearMode = {"Year", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020"};
    private String dayString, monthString, yearString, genderString;
    private SpinKitView spinKit;
    private String displayType;
    private boolean flag = false, isNameAvailable;
    private UserDetailPresenter userDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        context = this;

        if (getIntent() != null) {
            displayType = getIntent().getStringExtra("displayType");
        }

        inUi();
        setScreenLayout();
        setDateSpinner();
    }

    private void inUi() {
        userDetailPresenter = new UserDetailPresenter(context, this);
        spinKit = findViewById(R.id.spinKit);
        dobRL = findViewById(R.id.dobRL);
        dobLL = findViewById(R.id.dobLL);
        genderRL = findViewById(R.id.genderRL);
        saveButton = findViewById(R.id.saveButton);
        nextButton = findViewById(R.id.nextButton);
        maleRL = findViewById(R.id.maleRL);
        femaleRL = findViewById(R.id.femaleRL);
        otherRL = findViewById(R.id.otherRL);
        maleIV = findViewById(R.id.maleIV);
        femaleIV = findViewById(R.id.femaleIV);
        otherIV = findViewById(R.id.otherIV);
        dobTV = findViewById(R.id.dobTV);
        nameET = findViewById(R.id.nameET);

        saveButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        maleRL.setOnClickListener(this);
        femaleRL.setOnClickListener(this);
        otherRL.setOnClickListener(this);
    }

    private void setScreenLayout() {
        if (displayType.equalsIgnoreCase("all")){
            saveButton.setVisibility(View.GONE);
            if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.USER_NAME_DETAIL))){
                dobTV.setText("Date of birth");
                nameET.setVisibility(View.GONE);
                isNameAvailable = true;
            } else {
                isNameAvailable = false;
                dobTV.setText("Name and date of birth");
            }
        } else {
            nextButton.setVisibility(View.GONE);
            dobLL.setVisibility(View.GONE);
            dobTV.setText("Name");
        }
    }

    private void setDateSpinner() {
        daySpinner = findViewById(R.id.daySpinner);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dayMode);
        dayAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        daySpinner.setAdapter(dayAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ((TextView) daySpinner.getSelectedView()).setTextColor(Color.parseColor("#4c4c4c"));
                    ((TextView) daySpinner.getSelectedView()).setTextSize(14);
                    ((TextView) daySpinner.getSelectedView()).setGravity(Gravity.CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dayString = daySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        monthSpinner = findViewById(R.id.monthSpinner);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, monthMode);
        monthAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        monthSpinner.setAdapter(monthAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ((TextView) monthSpinner.getSelectedView()).setTextColor(Color.parseColor("#4c4c4c"));
                    ((TextView) monthSpinner.getSelectedView()).setTextSize(14);
                    ((TextView) monthSpinner.getSelectedView()).setGravity(Gravity.CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                monthString = monthSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner = findViewById(R.id.yearSpinner);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, yearMode);
        yearAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ((TextView) yearSpinner.getSelectedView()).setTextColor(Color.parseColor("#4c4c4c"));
                    ((TextView) yearSpinner.getSelectedView()).setTextSize(14);
                    ((TextView) yearSpinner.getSelectedView()).setGravity(Gravity.CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                yearString = yearSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextButton:
                if (dayString.equalsIgnoreCase("Day")) {
                    showMessage("Please select day");
                } else {
                    if (monthString.equalsIgnoreCase("Month")) {
                        showMessage("Please select month");
                    } else {
                        if (yearString.equalsIgnoreCase("Year")) {
                            showMessage("Please select year");
                        } else {
                            if (isNameAvailable) {
                                dobRL.setVisibility(View.GONE);
                                genderRL.setVisibility(View.VISIBLE);
                                nextButton.setText("Save");
                                if (flag) {
                                    if (!Helper.isContainValue(genderString)) {
                                        showMessage("Please select gender");
                                    } else {
                                        callUpdateUserApi(dayString + "/" + monthString + "/" + yearString);
                                    }
                                } else {
                                    flag = true;
                                }
                            } else {
                                if (!Helper.isContainValue(nameET.getText().toString().trim())){
                                    showMessage("Please enter your name");
                                } else {
                                    dobRL.setVisibility(View.GONE);
                                    genderRL.setVisibility(View.VISIBLE);
                                    nextButton.setText("Save");
                                    if (flag) {
                                        if (!Helper.isContainValue(genderString)) {
                                            showMessage("Please select gender");
                                        } else {
                                            callUpdateUserWithNameApi(dayString + "/" + monthString + "/" + yearString, nameET.getText().toString().trim());
                                        }
                                    } else {
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case R.id.saveButton:
                if (!Helper.isContainValue(nameET.getText().toString().trim())){
                    showMessage("Please enter your name");
                } else {
                    callUpdateNameApi(nameET.getText().toString().trim());
                }
                break;

            case R.id.maleRL:
                maleIV.setColorFilter(getResources().getColor(R.color.colorWhite));
                femaleIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));
                otherIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));

                maleRL.setBackground(getResources().getDrawable(R.drawable.circle_gender_bg));
                femaleRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));
                otherRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));

                genderString = "M";
                break;

            case R.id.femaleRL:
                femaleIV.setColorFilter(getResources().getColor(R.color.colorWhite));
                maleIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));
                otherIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));

                femaleRL.setBackground(getResources().getDrawable(R.drawable.circle_gender_bg));
                maleRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));
                otherRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));

                genderString = "F";
                break;

            case R.id.otherRL:
                otherIV.setColorFilter(getResources().getColor(R.color.colorWhite));
                maleIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));
                femaleIV.setColorFilter(getResources().getColor(R.color.colorLightGrey));

                otherRL.setBackground(getResources().getDrawable(R.drawable.circle_gender_bg));
                maleRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));
                femaleRL.setBackground(getResources().getDrawable(R.drawable.circle_drawable));

                genderString = "O";
                break;

            default:
                break;
        }
    }

    private void callUpdateUserApi(String dob) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            userDetailPresenter.callUpdateUserApi(genderString, dob);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callUpdateUserWithNameApi(String dob, String name) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            userDetailPresenter.callUpdateUserWithNameApi(genderString, dob, name);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void callUpdateNameApi(String name) {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            userDetailPresenter.callUpdateNameApi(name);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUpdateData(UpdateUserResponse updateUserResponse) {
        if (!updateUserResponse.status.equalsIgnoreCase("fail")) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_DETAIL, "detailSaved");
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_NAME_DETAIL, "detailSaved");

            if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.MOBILE_NUMBER))){
                AppPreferences.getInstance(context).savePreferencesString(AppConstants.USER_MOBILE_DETAIL, "detailSaved");
                startActivity(new Intent(context, DashboardActivity.class));
            } else {
                startActivity(new Intent(context, UpdateMobileActivity.class));
            }
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        userDetailPresenter.onDestroyedView();
        super.onDestroy();
    }
}