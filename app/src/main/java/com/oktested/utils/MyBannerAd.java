package com.oktested.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class MyBannerAd {

    private static MyBannerAd myBannerAd;

    private MyBannerAd() {
    }  //private constructor.

    public static MyBannerAd getInstance() {
        if (myBannerAd == null) {
            myBannerAd = new MyBannerAd();
        }
        return myBannerAd;
    }

    public void getAd(Context context, RelativeLayout layout, boolean isMidAd, String adUnitId) {
        AdSize adaptiveSize;
        PublisherAdView adView = new PublisherAdView(context);
        if (!isMidAd) {
            adaptiveSize = getAdSize((Activity) context);
        } else {
            adaptiveSize = AdSize.MEDIUM_RECTANGLE;
        }
        adView.setAdSizes(adaptiveSize);
        adView.setAdUnitId(adUnitId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("errorCode", "code = " + errorCode);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        PublisherAdRequest adRequest;
        if (Helper.isDebuggableMode(context)) {
            adRequest = new PublisherAdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID))
                    .build();
        } else {
            adRequest = new PublisherAdRequest.Builder().build();
        }
        adView.loadAd(adRequest);
        layout.addView(adView);
    }

    private AdSize getAdSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}