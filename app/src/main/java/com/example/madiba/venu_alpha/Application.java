package com.example.madiba.venu_alpha;

import android.support.multidex.MultiDexApplication;

import timber.log.Timber;


/**
 * Created by Madiba on 12/1/2016.
 */

public class Application extends MultiDexApplication  {
    @Override
    public void onCreate() {

        super.onCreate();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }

}
