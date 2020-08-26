package com.bplaz.merchant.FirebaseNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bplaz.merchant.Activity.SplashScreen.SplashScreen;

public class MyNotificationPublisher extends BroadcastReceiver {
    public void onReceive (Context context , Intent intent) {
        String action = intent.getAction();
        if (action.equals("open_activity")) {
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(context, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }
}
