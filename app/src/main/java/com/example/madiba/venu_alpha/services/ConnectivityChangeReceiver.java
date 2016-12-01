package com.example.madiba.venu_alpha.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.example.madiba.venu_alpha.Actions.ActionNetwork;
import com.example.madiba.venu_alpha.utils.NetUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Madiba on 11/22/2016.
 */
 public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.v("talon_pull", "connectivity change: just starting receiver");

//        AppSettings settings = AppSettings.getInstance(context);
//
//        // we don't want to do anything here if talon pull isn't on
//        if (!settings.pushNotifications) {
//            Log.v("talon_pull", "connectivity change: stopping the receiver very early");
//            return;
//        }

        if (NetUtils.hasInternetConnection(context)) {
            Log.v("talon_pull", "connectivity change: network is available and talon pull is on");

            // we want to turn off the live streaming/talon pull because it is just wasting battery not working/looking for connection
//            context.sendBroadcast(new Intent("com.klinker.android.twitter.STOP_PUSH_SERVICE"));

            EventBus.getDefault().post(new ActionNetwork(true));

        } else {
            Log.v("talon_pull", "connectivity change: network not available but talon pull is on");
            EventBus.getDefault().post(new ActionNetwork(false));

            // we want to turn off the live streaming/talon pull because it is just wasting battery not working/looking for connection
//            context.sendBroadcast(new Intent("com.klinker.android.twitter.STOP_PUSH_SERVICE"));
        }
    }
}