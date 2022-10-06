package com.oktested.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.oktested.R;
import com.oktested.community.ui.PostDetailActivity;
import com.oktested.quizDetail.ui.QuizDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;

public class DeepLinkActivity extends AppCompatActivity {

    private Context context;
    private String postId, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
        context = this;
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String urlStory = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && urlStory != null) {
            getVideoIdAndTypeFromString(urlStory);
            if (Helper.isContainValue(type)) {
                if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN))) {
                    if (type.equalsIgnoreCase("videos")) {
                        startActivity(new Intent(DeepLinkActivity.this, VideoPlayerActivity.class).putExtra("videoId", postId));
                    } else if (type.equalsIgnoreCase("posts")) {
                        startActivity(new Intent(DeepLinkActivity.this, PostDetailActivity.class).putExtra("postId", postId));
                    } else if (type.equalsIgnoreCase("quiz")) {
                        startActivity(new Intent(DeepLinkActivity.this, QuizDetailActivity.class).putExtra("articleId", postId));
                    } else {
                        startActivity(new Intent(DeepLinkActivity.this, SplashActivity.class).putExtra("urlReffer",urlStory));
                    }
                } else {
                      startActivity(new Intent(DeepLinkActivity.this, SplashActivity.class).putExtra("urlReffer",urlStory));
                }
            }
        }
        finish();
    }

    private void getVideoIdAndTypeFromString(String input) {
        try {
            String[] inputArray = input.split("/");
            postId = inputArray[inputArray.length - 1];
            type = inputArray[inputArray.length - 2];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}