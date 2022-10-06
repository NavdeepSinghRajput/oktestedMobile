package com.oktested.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

public class CommunityApplicationSelectorReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        for (String key : Objects.requireNonNull(intent.getExtras()).keySet()) {
            try {
                ComponentName componentInfo = (ComponentName) intent.getExtras().get(key);
                PackageManager packageManager = context.getPackageManager();
                assert componentInfo != null;
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(componentInfo.getPackageName(), PackageManager.GET_META_DATA));

                String from = intent.getExtras().getString("from");
                String postId = intent.getExtras().getString("postId");
                String communityId = intent.getExtras().getString("communityId");
                int position = intent.getExtras().getInt("position");

                Intent shareIntent;
                if (Helper.isContainValue(from) && from.equalsIgnoreCase("post")) {
                    shareIntent = new Intent(AppConstants.POST_SHARE_COUNT_UPDATE);
                } else {
                    shareIntent = new Intent(AppConstants.LIST_SHARE_COUNT_UPDATE);
                }
                shareIntent.putExtra("appName", appName);
                shareIntent.putExtra("postId", postId);
                shareIntent.putExtra("communityId", communityId);
                shareIntent.putExtra("position", position);

                LocalBroadcastManager.getInstance(context).sendBroadcast(shareIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}