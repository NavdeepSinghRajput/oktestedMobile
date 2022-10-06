package com.oktested.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.oktested.R;

public class MyExitInterstitialAd {

    private static MyExitInterstitialAd myExitInterstitialAd;
    private PublisherInterstitialAd interstitial;
    private OnExitAdClosed onExitAdClosed;

    private MyExitInterstitialAd() {
    }  //private constructor.

    public void setInterface(OnExitAdClosed onExitAdClosed) {
        this.onExitAdClosed = onExitAdClosed;
    }

    public static MyExitInterstitialAd getInstance() {
        if (myExitInterstitialAd == null) {
            myExitInterstitialAd = new MyExitInterstitialAd();
        }
        return myExitInterstitialAd;
    }

    public void loadExitInterstitialAd(Context context, String adCode) {
        interstitial = new PublisherInterstitialAd(context);
        PublisherAdRequest adRequest;
        if (Helper.isDebuggableMode(context)) {
            adRequest = new PublisherAdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID))
                    .build();
        } else {
            adRequest = new PublisherAdRequest.Builder().build();
        }

        interstitial.setAdUnitId(adCode);
        interstitial.loadAd(adRequest);

        interstitial.setAdListener(new AdListener() {
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
                Log.d("onAdLoaded", "exitAd");
                // Code to be executed when the ad is displayed.
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
                if (onExitAdClosed != null) {
                    onExitAdClosed.onExitInterstitialAdClosed();
                }
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }

    public interface OnExitAdClosed {
        void onExitInterstitialAdClosed();
    }

    public PublisherInterstitialAd getAd() {
        return interstitial;
    }
}