package com.cc.cachedemo;

import android.app.Application;

import com.cc.cachedemo.utils.CrashHandler;

/**
 * Created by cc on 2016/9/18.
 */
public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CrashHandler.getInstance().init(this);
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

}
