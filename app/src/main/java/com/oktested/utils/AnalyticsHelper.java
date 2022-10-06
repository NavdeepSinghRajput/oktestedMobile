package com.oktested.utils;

import android.content.Context;
import android.os.Bundle;

import java.util.Calendar;

public class AnalyticsHelper {

    private Context mContext;
    private static volatile AnalyticsHelper instance;

    private AnalyticsHelper(Context context) {
        mContext = context;
    }

    public synchronized static AnalyticsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AnalyticsHelper(context);
        }
        return instance;
    }

    public void trackPlayVideoFcmEvent(String videoId) {
        try {
            Bundle params = new Bundle();
            params.putString("videoId", videoId);
            params.putString("device_type", "android");

            Calendar calendar = Calendar.getInstance();
            params.putString("time", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            FireBaseHelper.getFcmAnalyticsInstance(mContext).logEvent("play_video", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trackCastVideoFcmEvent(String videoId) {
        try {
            Bundle params = new Bundle();
            params.putString("videoId", videoId);
            params.putString("device_type", "android");
            FireBaseHelper.getFcmAnalyticsInstance(mContext).logEvent("cast_video", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trackCaptionFcmEvent(String status) {
        try {
            Bundle params = new Bundle();
            params.putString("status", status);
            params.putString("device_type", "android");
            FireBaseHelper.getFcmAnalyticsInstance(mContext).logEvent("caption_event", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trackShareVideoFcmEvent(String platform) {
        try {
            Bundle params = new Bundle();
            params.putString("platform", platform);
            params.putString("device_type", "android");
            FireBaseHelper.getFcmAnalyticsInstance(mContext).logEvent("share_video", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}