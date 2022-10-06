package com.oktested.dashboard.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.oktested.BuildConfig;
import com.oktested.R;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.dashboard.presenter.DashboardPresenter;
import com.oktested.entity.AdSettingDataResponse;
import com.oktested.entity.AppUpdate;
import com.oktested.favourite.ui.FavouriteFragment;
import com.oktested.home.model.NotifyInviteFriendResponse;
import com.oktested.home.ui.HomeFragment;
import com.oktested.home.ui.VideosFragment;
import com.oktested.login.ui.LoginActivity;
import com.oktested.menu.ui.MenuFragment;
import com.oktested.notification.ui.NotificationFragment;
import com.oktested.search.ui.SearchFragment;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;
import com.oktested.utils.MyExitInterstitialAd;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements DashboardView, BottomNavigationView.OnNavigationItemSelectedListener {

    private Fragment homeFragment, notificationFragment, favouriteFragment, searchFragment, menuFragment, activeFragment;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private boolean doubleBackToExitPressedOnce = false, isFireBaseTokenUpdated = false, isExitAdLoaded = false, loadNotificationTab, onlineFirstTime = true;
    private SpinKitView spinKitView;
    private Context context;
    private RelativeLayout parentRL;
    private TextView thankYouTV;
    private DashboardPresenter dashboardPresenter;
    private View badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context = this;

        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.HOME_PAGE_LOADED))) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.HOME_PAGE_LOADED, "");
            finish();
            startActivity(getIntent());
        }

        dashboardPresenter = new DashboardPresenter(this, DashboardActivity.this);
        spinKitView = findViewById(R.id.spinKit);
        parentRL = findViewById(R.id.parentRL);
        thankYouTV = findViewById(R.id.thankYouTV);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        callUserDataApi();
        accessFineLocationPermission();
        setDefaultValues();
        loadAds();
        if (getIntent().getExtras() != null) {
            loadNotificationTab = getIntent().getExtras().getBoolean("loadNotificationTab");
        }

        String referralCode = AppPreferences.getInstance(context).getPreferencesString(AppConstants.QUIZ_INVITE_LINK);
        if (Helper.isContainValue(referralCode)) {
            dashboardPresenter.notifyInviteLink(referralCode);
        }

        // For in app update
        if (Build.VERSION.SDK_INT >= 21) {
            doAppUpdate();
        }

        // For update via custom alert dialog
        if (DataHolder.getInstance().appUpdate != null && DataHolder.getInstance().appUpdate.version_code > BuildConfig.VERSION_CODE) {
            showAlertDialogForUpdateApp(DataHolder.getInstance().appUpdate);
        }
    }

    private void setBadge() {
        badge = LayoutInflater.from(this).inflate(R.layout.component_badge, bottomNavigationView, false);
        bottomNavigationView.addView(badge);
    }

    public void removeBadge() {
        bottomNavigationView.removeView(badge);
    }

    private void setUserOnlineOfflineStatus() {
        ProcessLifecycleOwner.get().getLifecycle()
                .addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        HashMap<String, String> headerMap = new HashMap<>();
                        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
                        if (event == Lifecycle.Event.ON_STOP) {
                            Call<ResponseBody> responseBodyCall = NetworkManager.getApi().userOffline(headerMap);
                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    // System.out.println("offlinesuccess");
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // System.out.println("offlineFailre" + t.getMessage());
                                }
                            });
                        } else if (event == Lifecycle.Event.ON_START) {
                            Call<ResponseBody> responseBodyCall;
                            if (onlineFirstTime) {
                                onlineFirstTime = false;
                                responseBodyCall = NetworkManager.getApi().userOnlineFirstTime(headerMap);
                            } else {
                                responseBodyCall = NetworkManager.getApi().userOnline(headerMap);
                            }

                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    // System.out.println("onlineSuccess");
                                    Intent intent = new Intent();
                                    intent.setAction(getString(R.string.app_name) + "user.refresh");
                                    sendBroadcast(intent);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    //  System.out.println("onlineFailure" + t.getMessage());
                                }
                            });
                        }
                    }
                });
    }

    private void loadAds() {
        if (DataHolder.getInstance().adSettingResponse != null && DataHolder.getInstance().adSettingResponse.enable && DataHolder.getInstance().adSettingResponse.data != null && DataHolder.getInstance().adSettingResponse.data.size() > 0) {
            for (int i = 0; i < DataHolder.getInstance().adSettingResponse.data.size(); i++) {
                DataHolder.getInstance().adSettingValueHashMap.put(DataHolder.getInstance().adSettingResponse.data.get(i).ad_type, DataHolder.getInstance().adSettingResponse.data.get(i).value);
            }
        }

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.EXIT_INTERSTITIAL_AD)) {
            loadExitInterstitialAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.EXIT_INTERSTITIAL_AD));
        }

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.INTERSTITIAL_AD)) {
            AdSettingDataResponse.AdSettingValue adSettingValue = DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.INTERSTITIAL_AD);
            if (adSettingValue != null && adSettingValue.status) {
                AppConstants.INTERSTITIAL_INCREASE_AD_COUNT = adSettingValue.count;
            }
        }
    }

    private void doAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                   /* try {
                        if (AppConstants.IS_FORCE_UPDATE) {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, DashboardActivity.this, AppConstants.REQUEST_CODE_UPDATE);
                        } else {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, DashboardActivity.this, AppConstants.REQUEST_CODE_UPDATE);
                        }
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }*/
                    Log.v("update", "available");
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    Log.v("update", "not available");
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UNKNOWN) {
                    Log.v("update", "unknown");
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    Log.v("update", "in progress");
                }
            }
        });

        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.v("update", "failed");
            }
        });
    }

    private void setDefaultValues() {
        AppPreferences.getInstance(context).savePreferencesString(AppConstants.VIDEO_HEIGHT, "Auto");
        AppPreferences.getInstance(context).savePreferencesString(AppConstants.VIDEO_WIDTH, "Auto");
        AppConstants.CAPTION_STATUS = "On";
        AppConstants.IS_VIDEO_CASTED = true;
        AppConstants.INTERSTITIAL_AD_COUNT = 1;
    }

    private void callUserDataApi() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            dashboardPresenter.callUserData();
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    private void accessFineLocationPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 112);
        }
    }

    private void addFragments() {
        AppPreferences.getInstance(context).savePreferencesString(AppConstants.HOME_PAGE_LOADED, "dataLoaded");
        fragmentManager = getSupportFragmentManager();
        // if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.COMMUNITY_VISIBLE).equalsIgnoreCase("true")) {
        homeFragment = new HomeFragment();
     /*   } else {
            homeFragment = new VideosFragment();
        }*/
        notificationFragment = new NotificationFragment();
        favouriteFragment = new FavouriteFragment();
        searchFragment = new SearchFragment();
        menuFragment = new MenuFragment();

        try {
            fragmentManager.beginTransaction().add(R.id.frame_container, searchFragment, "search").hide(searchFragment).commitAllowingStateLoss();
            fragmentManager.beginTransaction().add(R.id.frame_container, favouriteFragment, "favourite").hide(favouriteFragment).commitAllowingStateLoss();
            fragmentManager.beginTransaction().add(R.id.frame_container, menuFragment, "menu").hide(menuFragment).commitAllowingStateLoss();

            if (loadNotificationTab) {
                activeFragment = notificationFragment;
                fragmentManager.beginTransaction().add(R.id.frame_container, homeFragment, "home").hide(homeFragment).commitAllowingStateLoss();
                fragmentManager.beginTransaction().add(R.id.frame_container, notificationFragment, "notification").commitAllowingStateLoss();
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                new Handler().postDelayed(() -> {
                    notificationFragment.setUserVisibleHint(true);
                }, 2000);
            } else {
                activeFragment = homeFragment;
                fragmentManager.beginTransaction().add(R.id.frame_container, homeFragment, "home").commitAllowingStateLoss();
                fragmentManager.beginTransaction().add(R.id.frame_container, notificationFragment, "notification").hide(notificationFragment).commitAllowingStateLoss();

                if (Helper.isContainValue(DataHolder.getInstance().getUserDataresponse.notification)) {
                    setBadge();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DataHolder.getInstance().adSettingValueHashMap.containsKey(AppConstants.HP_BOTTOM_BANNER_AD)) {
            loadBottomAd(DataHolder.getInstance().adSettingValueHashMap.get(AppConstants.HP_BOTTOM_BANNER_AD));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.COMMUNITY_VISIBLE).equalsIgnoreCase("true")) {
                    if (!(activeFragment instanceof HomeFragment)) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                        transaction.hide(activeFragment).show(homeFragment).commit();
                        activeFragment = homeFragment;
                    }
                } else {
                    if (!(activeFragment instanceof VideosFragment)) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                        transaction.hide(activeFragment).show(homeFragment).commit();
                        activeFragment = homeFragment;
                    }
                }
                return true;

            case R.id.navigation_menu:
                if (!(activeFragment instanceof MenuFragment)) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                    transaction.hide(activeFragment).show(menuFragment).commit();
                    activeFragment = menuFragment;

                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstants.PAUSE_PLAYER));
                }
                return true;

            case R.id.navigation_inbox:
                if (!(activeFragment instanceof NotificationFragment)) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                    transaction.hide(activeFragment).show(notificationFragment).commit();
                    activeFragment = notificationFragment;

                    notificationFragment.setUserVisibleHint(true);
                    removeBadge();

                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstants.PAUSE_PLAYER));
                }
                return true;

            case R.id.navigation_search:
                if (!(activeFragment instanceof SearchFragment)) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                    transaction.hide(activeFragment).show(searchFragment).commit();
                    activeFragment = searchFragment;

                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstants.PAUSE_PLAYER));
                }
                return true;

            case R.id.navigation_favourite:
                if (!(activeFragment instanceof FavouriteFragment)) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_up, 0);
                    transaction.hide(activeFragment).show(favouriteFragment).commit();
                    activeFragment = favouriteFragment;

                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(AppConstants.PAUSE_PLAYER));
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (AppPreferences.getInstance(context).getPreferencesString(AppConstants.COMMUNITY_VISIBLE).equalsIgnoreCase("true")) {
            if (activeFragment == null || activeFragment instanceof HomeFragment) {
                if (doubleBackToExitPressedOnce) {
                    if (isExitAdLoaded) {
                        // Show exit interstitial ad
                        PublisherInterstitialAd interstitialAd = MyExitInterstitialAd.getInstance().getAd();
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    parentRL.setVisibility(View.GONE);
                                    thankYouTV.setVisibility(View.VISIBLE);
                                }
                            }, 400);

                            MyExitInterstitialAd.getInstance().setInterface(new MyExitInterstitialAd.OnExitAdClosed() {
                                @Override
                                public void onExitInterstitialAdClosed() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }
                            });
                        } else {
                            super.onBackPressed();
                        }
                    } else {
                        super.onBackPressed();
                    }
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 3000);
            } else {
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
            }
        } else {
            if (activeFragment == null || activeFragment instanceof VideosFragment) {
                if (doubleBackToExitPressedOnce) {
                    if (isExitAdLoaded) {
                        // Show exit interstitial ad
                        PublisherInterstitialAd interstitialAd = MyExitInterstitialAd.getInstance().getAd();
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    parentRL.setVisibility(View.GONE);
                                    thankYouTV.setVisibility(View.VISIBLE);
                                }
                            }, 400);

                            MyExitInterstitialAd.getInstance().setInterface(new MyExitInterstitialAd.OnExitAdClosed() {
                                @Override
                                public void onExitInterstitialAdClosed() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }
                            });
                        } else {
                            super.onBackPressed();
                        }
                    } else {
                        super.onBackPressed();
                    }
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 3000);
            } else {
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
            }
        }
    }

    @Override
    public void getUserData(GetUserResponse getUserResponse) {
        if (getUserResponse != null && getUserResponse.data != null) {
            DataHolder.getInstance().getUserDataresponse = getUserResponse.data;
            if (!getUserResponse.data.is_blocked) {
                if (getUserResponse.data.firebase_token != null && getUserResponse.data.firebase_token.size() > 0) {
                    for (int i = 0; i < getUserResponse.data.firebase_token.size(); i++) {
                        if (getUserResponse.data.firebase_token.get(i).equalsIgnoreCase(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            addFragments();
                            isFireBaseTokenUpdated = true;
                            if (getUserResponse.data.is_quiz_enable) {
                                setUserOnlineOfflineStatus();
                            }
                            break;
                        }
                    }

                    if (!isFireBaseTokenUpdated) {
                        callUpdateUserApi();
                    }
                } else {
                    callUpdateUserApi();
                }
            } else {
                callLogout();
            }
        }
    }

    private void callLogout() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        Call<ResponseBody> responseBodyCall = NetworkManager.getApi().userOffline(headerMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("offlinesuccess");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("offlineFailre" + t.getMessage());
            }
        });

        AppPreferences.getInstance(context).clearPreferences();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        finish();
    }

    @Override
    public void setUpdateData(UpdateUserResponse updateUserResponse) {
        if (!updateUserResponse.status.equalsIgnoreCase("fail")) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            addFragments();
            if (DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
                setUserOnlineOfflineStatus();
            }
        }
    }

    @Override
    public void setNotifyInviteLink(NotifyInviteFriendResponse notifyInviteFriendResponse) {
        if (notifyInviteFriendResponse != null && notifyInviteFriendResponse.status.equalsIgnoreCase("1")) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.QUIZ_INVITE_LINK, "");
            //  sectionMap.put(AppConstants.SECTION_QUIZ_TRENDING, trendingQuizResponse);
        }
    }

    private void callUpdateUserApi() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            dashboardPresenter.callUpdateUserApi();
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (dashboardPresenter != null) {
            dashboardPresenter.onDestroyedView();
        }
        super.onDestroy();
    }

    private void showAlertDialogForUpdateApp(AppUpdate appUpdate) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        AlertDialog alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(appUpdate.title);
        alertDialog.setMessage(appUpdate.message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        redirectStore();
                    }
                });
        if (!appUpdate.force_update) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No, thanks",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void redirectStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.oktested"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadExitInterstitialAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            MyExitInterstitialAd.getInstance().loadExitInterstitialAd(context, adSettingValue.ad_code);
            isExitAdLoaded = true;
        }
    }

    private void loadBottomAd(AdSettingDataResponse.AdSettingValue adSettingValue) {
        if (adSettingValue != null && adSettingValue.status) {
            RelativeLayout bottomAdView = findViewById(R.id.bottomAdView);
            MyBannerAd.getInstance().getAd(context, bottomAdView, adSettingValue.is_mid_ad, adSettingValue.ad_code);
        }
    }
}