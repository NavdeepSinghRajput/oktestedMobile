package com.oktested.menu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.oktested.BuildConfig;
import com.oktested.R;
import com.oktested.browseAnchor.ui.BrowseAnchorActivity;
import com.oktested.browseShow.ui.BrowseShowActivity;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.helpSupport.ui.HelpSupportActivity;
import com.oktested.login.ui.LoginActivity;
import com.oktested.menu.presenter.MenuPresenter;
import com.oktested.notification.ui.NotificationActivity;
import com.oktested.profile.ui.ProfileActivity;
import com.oktested.reportProblem.ui.ReportProblemActivity;
import com.oktested.termsPrivacy.ui.TermsPrivacyActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment implements MenuView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    private MenuPresenter menuPresenter;
    private Spinner videoQualitySpinner;
    private String[] videoMode = {"Auto", "1080p\nHD", "720p\nHD", "480p", "360p", "240p", "144p"};
    private TextView newVideoOffTV, newVideoOnTV, favShowsOffTV, favShowsOnTV, favAnchorOffTV, favAnchorOnTV, notificationTV;
    private Switch newVideoSwitch, favShowsSwitch, favAnchorSwitch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_menu, container, false);

        inUi(root_view);
        //setVideoQualitySpinner(root_view);
        getUserData(DataHolder.getInstance().getUserDataresponse);

        return root_view;
    }

    private void setVideoQualitySpinner(View root_view) {
        videoQualitySpinner = root_view.findViewById(R.id.videoQualitySpinner);
        ArrayAdapter<String> videoQualityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, videoMode);
        videoQualityAdapter.setDropDownViewResource(R.layout.spinner_item);
        videoQualitySpinner.setAdapter(videoQualityAdapter);

        videoQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) videoQualitySpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.colorWhite));
                ((TextView) videoQualitySpinner.getSelectedView()).setTextSize(10);
                ((TextView) videoQualitySpinner.getSelectedView()).setGravity(Gravity.CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void inUi(View root_view) {
        menuPresenter = new MenuPresenter(context, this);

        newVideoOffTV = root_view.findViewById(R.id.newVideoOffTV);
        newVideoOnTV = root_view.findViewById(R.id.newVideoOnTV);
        favShowsOffTV = root_view.findViewById(R.id.favShowsOffTV);
        favShowsOnTV = root_view.findViewById(R.id.favShowsOnTV);
        favAnchorOffTV = root_view.findViewById(R.id.favAnchorOffTV);
        favAnchorOnTV = root_view.findViewById(R.id.favAnchorOnTV);
        notificationTV = root_view.findViewById(R.id.notificationTV);

        TextView clearCacheTV = root_view.findViewById(R.id.clearCacheTV);
        TextView termsConditionTV = root_view.findViewById(R.id.termsConditionTV);
        TextView privacyPolicyTV = root_view.findViewById(R.id.privacyPolicyTV);
        TextView helpSupportTV = root_view.findViewById(R.id.helpSupportTV);
        TextView reportProblemTV = root_view.findViewById(R.id.reportProblemTV);
        TextView logoutTV = root_view.findViewById(R.id.logoutTV);
        TextView appVersionTV = root_view.findViewById(R.id.appVersionTV);
        appVersionTV.setText("@" + Calendar.getInstance().get(Calendar.YEAR) + " ScoopWhoop Media Pvt. Ltd.\nApp Version : " + BuildConfig.VERSION_NAME);

        newVideoSwitch = root_view.findViewById(R.id.newVideoSwitch);
        favShowsSwitch = root_view.findViewById(R.id.favShowsSwitch);
        favAnchorSwitch = root_view.findViewById(R.id.favAnchorSwitch);

        LinearLayout browseAnchorsLL = root_view.findViewById(R.id.browseAnchorsLL);
        LinearLayout browseShowsLL = root_view.findViewById(R.id.browseShowsLL);
        LinearLayout browseProfileLL = root_view.findViewById(R.id.browseProfileLL);

        clearCacheTV.setOnClickListener(this);
        termsConditionTV.setOnClickListener(this);
        privacyPolicyTV.setOnClickListener(this);
        helpSupportTV.setOnClickListener(this);
        reportProblemTV.setOnClickListener(this);
        logoutTV.setOnClickListener(this);

        browseAnchorsLL.setOnClickListener(this);
        browseShowsLL.setOnClickListener(this);
        browseProfileLL.setOnClickListener(this);
        notificationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
        newVideoSwitch.setOnCheckedChangeListener(this);
        favShowsSwitch.setOnCheckedChangeListener(this);
        favAnchorSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.browseProfileLL:
                intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("screenName", AppConstants.SECTION_SHOWS_LAYOUT);
                intent.putExtra("heading", "Our Shows");
                startActivity(intent);
                break;
            case R.id.browseShowsLL:
                intent = new Intent(context, BrowseShowActivity.class);
                intent.putExtra("screenName", AppConstants.SECTION_SHOWS_LAYOUT);
                intent.putExtra("heading", "Our Shows");
                startActivity(intent);
                break;

            case R.id.browseAnchorsLL:
                intent = new Intent(context, BrowseAnchorActivity.class);
                startActivity(intent);
                break;

            case R.id.clearCacheTV:
                clearCache();
                break;

            case R.id.termsConditionTV:
                intent = new Intent(context, TermsPrivacyActivity.class);
                intent.putExtra("from", "terms");
                startActivity(intent);
                break;

            case R.id.privacyPolicyTV:
                intent = new Intent(context, TermsPrivacyActivity.class);
                intent.putExtra("from", "privacy");
                startActivity(intent);
                break;

            case R.id.helpSupportTV:
                intent = new Intent(context, HelpSupportActivity.class);
                startActivity(intent);
                break;

            case R.id.reportProblemTV:
                intent = new Intent(context, ReportProblemActivity.class);
                startActivity(intent);
                break;

            case R.id.logoutTV:
                logout();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (menuPresenter != null) {
            menuPresenter.onDestroyedView();
        }
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.newVideoSwitch:
                if (Helper.isNetworkAvailable(context)) {
                    if (isChecked) {
                        menuPresenter.callNotificationSubscribe("video");

                        newVideoOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                        newVideoOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
                    } else {
                        menuPresenter.callNotificationUnsubscribe("video");

                        newVideoOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                        newVideoOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
                break;

            case R.id.favShowsSwitch:
                if (Helper.isNetworkAvailable(context)) {
                    if (isChecked) {
                        menuPresenter.callNotificationSubscribe("show");

                        favShowsOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                        favShowsOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
                    } else {
                        menuPresenter.callNotificationUnsubscribe("show");

                        favShowsOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                        favShowsOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
                break;

            case R.id.favAnchorSwitch:
                if (Helper.isNetworkAvailable(context)) {
                    if (isChecked) {
                        menuPresenter.callNotificationSubscribe("actor");

                        favAnchorOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                        favAnchorOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
                    } else {
                        menuPresenter.callNotificationUnsubscribe("actor");

                        favAnchorOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                        favAnchorOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                } else {
                    showMessage(getString(R.string.please_check_internet_connection));
                }
                break;

            default:
                break;
        }
    }

    private void getUserData(GetUserDataresponse response) {
        if (response != null && response.subscribe != null && response.subscribe.size() > 0) {
            for (int i = 0; i < response.subscribe.size(); i++) {
                if (response.subscribe.get(i).equalsIgnoreCase("video")) {
                    newVideoSwitch.setChecked(true);
                } else if (response.subscribe.get(i).equalsIgnoreCase("show")) {
                    favShowsSwitch.setChecked(true);
                } else if (response.subscribe.get(i).equalsIgnoreCase("actor")) {
                    favAnchorSwitch.setChecked(true);
                }
            }

            if (newVideoSwitch.isChecked()) {
                newVideoOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                newVideoOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
            } else {
                newVideoOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                newVideoOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
            }

            if (favShowsSwitch.isChecked()) {
                favShowsOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                favShowsOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
            } else {
                favShowsOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                favShowsOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
            }

            if (favAnchorSwitch.isChecked()) {
                favAnchorOnTV.setTextColor(getResources().getColor(R.color.colorWhite));
                favAnchorOffTV.setTextColor(getResources().getColor(R.color.colorGrey));
            } else {
                favAnchorOnTV.setTextColor(getResources().getColor(R.color.colorGrey));
                favAnchorOffTV.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    setUserOnlineOfflineStatus();
                    AppPreferences.getInstance(context).clearPreferences();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setUserOnlineOfflineStatus() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        Call<ResponseBody> responseBodyCall = NetworkManager.getApi().userOffline(headerMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
             //   System.out.println("offlinesuccess");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              //  System.out.println("offlineFailre" + t.getMessage());
            }
        });
    }


    private void clearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.clear_cache_text))
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    File dir = context.getCacheDir();
                    if (dir != null && dir.isDirectory()) {
                        deleteDir(dir);
                        if (deleteDir(dir)) {
                            showMessage("Cache Cleared");
                        }
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}