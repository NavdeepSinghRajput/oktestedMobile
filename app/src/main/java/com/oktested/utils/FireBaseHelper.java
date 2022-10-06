package com.oktested.utils;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireBaseHelper {

    private static FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    public FireBaseHelper(Context context) {
        this.context = context;
    }

    static FirebaseAnalytics getFcmAnalyticsInstance(Context context) {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
        }
        return mFirebaseAnalytics;
    }
}