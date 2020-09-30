package com.bateriku.bplazmerchant.Connection;

import android.app.Application;
import android.content.Context;

import com.bateriku.bplazmerchant.R;

public class BasedURL extends Application {

    private static Context mContext;
    public static String ROOT_URL;
    public static String ROOT_URL_IMAGE;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        ROOT_URL = getString(R.string.ROOT_URL);
        ROOT_URL_IMAGE = getString(R.string.ROOT_URL_IMAGE);
    }

    public static Context getContext(){
        return mContext;
    }

}
