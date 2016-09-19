package com.cc.cachedemo.config;

import android.os.Environment;

/**
 * Created by cc on 2016/9/18.
 */
public class AppConfig {

    public static boolean DEBUG = true;

    public static String BASE_URL = "http://gc.ditu.aliyun.com/";

    public static String HTTP_CACHE_PAth = Environment.getExternalStorageDirectory()+"/cache";

    public static int CACHE_SIZE = 1024*1024*2;



}
