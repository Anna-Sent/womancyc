package com.anna.sent.soft.womancyc;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class WcApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.adMobAppId));
    }
}
