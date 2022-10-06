package com.oktested.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.oktested.R;
import com.oktested.community.ui.PostDetailActivity;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.entity.AdSettingResponse;
import com.oktested.entity.AppSetting;
import com.oktested.entity.AppUpdate;
import com.oktested.login.ui.LoginActivity;
import com.oktested.updateMobile.ui.UpdateMobileActivity;
import com.oktested.userDetail.ui.UserDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;

import org.json.JSONObject;

import java.util.Objects;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private VideoView videoView;
    private String videoId, commentId, type, postId, notifyType;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
            //Log.d("hello", linkProperties.toString());
            try {
                Object s = linkProperties.opt("+non_branch_link");
                if (s != null) {
                    String[] createUrl = s.toString().split("://");
                    String[] createFinalValue = createUrl[2].split("/?link_click_id");
                    String finalUrl = createFinalValue[0];
                    String urlReferral = finalUrl.replace("?", "");
                    AppPreferences.getInstance(SplashActivity.this).savePreferencesString(AppConstants.QUIZ_INVITE_LINK, urlReferral);
                } else {
                    String urlReferral = linkProperties.optString("~referring_link");
                    if (Helper.isContainValue(urlReferral)) {
                        String[] referralArray = urlReferral.split("/");
                        String referralCode = referralArray[referralArray.length - 1];
                        AppPreferences.getInstance(SplashActivity.this).savePreferencesString(AppConstants.QUIZ_INVITE_LINK, referralCode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        context = this;
        videoView = findViewById(R.id.videoView);

        fetchRemoteConfig();
        AppPreferences.getInstance(context).savePreferencesString(AppConstants.HOME_PAGE_LOADED, "");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Error", "getInstanceId failed", task.getException());
                            return;
                        }
                        String refreshedToken = Objects.requireNonNull(task.getResult()).getToken();
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.FIREBASE_TOKEN, refreshedToken);
                        Log.d("Refresh Token", refreshedToken);
                    }
                });

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equalsIgnoreCase("videoId")) {
                    videoId = String.valueOf(getIntent().getExtras().get(key));
                } else if (key.equalsIgnoreCase("post_id")) {
                    postId = String.valueOf(getIntent().getExtras().get(key));
                } else if (key.equalsIgnoreCase("parent_id")) {
                    commentId = String.valueOf(getIntent().getExtras().get(key));
                } else if (key.equalsIgnoreCase("post_type")) {
                    type = String.valueOf(getIntent().getExtras().get(key));
                } else if (key.equalsIgnoreCase("notify_type")) {
                    notifyType = String.valueOf(getIntent().getExtras().get(key));
                }
            }
        }

        callVideo(0);
        //  Helper.generateKeyHAsh(context);
    }

    private void callVideo(int seek) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.seekTo(seek);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN))) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    if (!Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.USER_DETAIL))) {
                        startActivity(new Intent(SplashActivity.this, UserDetailActivity.class).putExtra("displayType", "all"));
                    } else if (!Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.USER_NAME_DETAIL))) {
                        startActivity(new Intent(SplashActivity.this, UserDetailActivity.class).putExtra("displayType", "name"));
                    } else if (!Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.USER_MOBILE_DETAIL))) {
                        startActivity(new Intent(SplashActivity.this, UpdateMobileActivity.class));
                    } else {
                        if (!Helper.isContainValue(videoId)) {
                            if (Helper.isContainValue(type)) {
                                if (type.equalsIgnoreCase("video")) {
                                    startActivity(new Intent(SplashActivity.this, VideoPlayerActivity.class).putExtra("videoId", postId).putExtra("commentId", commentId));
                                } else if (type.equalsIgnoreCase("community_post")) {
                                    startActivity(new Intent(SplashActivity.this, PostDetailActivity.class).putExtra("postId", postId).putExtra("commentId", commentId));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                }
                            } else if (Helper.isContainValue(notifyType)) {
                                startActivity(new Intent(SplashActivity.this, DashboardActivity.class).putExtra("loadNotificationTab", true));
                            } else {
                                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                            }
                        } else {
                            startActivity(new Intent(SplashActivity.this, VideoPlayerActivity.class).putExtra("videoId", videoId));
                        }
                    }
                }
                finish();
            }
        });
    }

    private void fetchRemoteConfig() {
        // Fetch singleton FirebaseRemoteConfig object
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10800)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        //setting the default values for the UI
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        setRemoteConfigValues();
    }

    private void setRemoteConfigValues() {
        String appSetting = firebaseRemoteConfig.getString(AppConstants.APP_SETTING);
        DataHolder.getInstance().appSetting = new Gson().fromJson(appSetting, AppSetting.class);
        if (DataHolder.getInstance().appSetting != null) {
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.COMMUNITY_VISIBLE, String.valueOf(DataHolder.getInstance().appSetting.is_community_visible));
        }

        String appUpdate = firebaseRemoteConfig.getString(AppConstants.UPDATE_AVAILABLE);
        DataHolder.getInstance().appUpdate = new Gson().fromJson(appUpdate, AppUpdate.class);

        String adSetting = firebaseRemoteConfig.getString(AppConstants.ADS_SETTING);
        DataHolder.getInstance().adSettingResponse = new Gson().fromJson(adSetting, AdSettingResponse.class);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    String appSetting = firebaseRemoteConfig.getString(AppConstants.APP_SETTING);
                    DataHolder.getInstance().appSetting = new Gson().fromJson(appSetting, AppSetting.class);
                    if (DataHolder.getInstance().appSetting != null) {
                        AppPreferences.getInstance(context).savePreferencesString(AppConstants.COMMUNITY_VISIBLE, String.valueOf(DataHolder.getInstance().appSetting.is_community_visible));
                    }

                    String appUpdate = firebaseRemoteConfig.getString(AppConstants.UPDATE_AVAILABLE);
                    DataHolder.getInstance().appUpdate = new Gson().fromJson(appUpdate, AppUpdate.class);

                    String adSetting = firebaseRemoteConfig.getString(AppConstants.ADS_SETTING);
                    DataHolder.getInstance().adSettingResponse = new Gson().fromJson(adSetting, AdSettingResponse.class);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        callVideo(videoView.getCurrentPosition());
        super.onResume();
    }
}