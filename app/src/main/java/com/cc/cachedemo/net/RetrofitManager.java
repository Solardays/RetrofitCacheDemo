package com.cc.cachedemo.net;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.cc.cachedemo.BaseApplication;
import com.cc.cachedemo.config.AppConfig;
import com.cc.cachedemo.net.interceptor.OfflineCacheInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cc on 2016/9/18.
 * retrofit 管理工具
 */
public class RetrofitManager {
    private static final String DOMAIN = "cc";//文件存放总目录
    private static final String CACHE_FLOD_NAME = "crash";
    public static int CACHE_SIZE = 1024*1024*10;

    private static RetrofitManager mRetrofitManager;
    private Retrofit mRetrofit;

    private RetrofitManager(){
        initRetrofit();
    }

    public static synchronized RetrofitManager getInstance(){
        if (mRetrofitManager == null){
            mRetrofitManager = new RetrofitManager();
        }
        return mRetrofitManager;
    }

    private void initRetrofit() {
        HttpLoggingInterceptor LoginInterceptor = new HttpLoggingInterceptor();
        LoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

//        builder.addInterceptor(new RspCheckInterceptor());
        if (AppConfig.DEBUG){
            builder.addInterceptor(LoginInterceptor); //添加retrofit日志打印
        }


        File cacheFile = new File(getFilePath());
        Cache cache = new Cache(cacheFile,CACHE_SIZE);
        builder.cache(cache);
        //添加缓存，无网络从缓存里获取列表数据
        builder.addInterceptor(new OfflineCacheInterceptor());
        builder.addNetworkInterceptor(new com.cc.cachedemo.net.interceptor.CacheInterceptor()); //添加缓存控制拦截器

        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
    }

    public <T> T createReq(Class<T> reqServer){
        return mRetrofit.create(reqServer);
    }

    @NonNull
    private String getFilePath() {
        String fileAbsPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
                !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            fileAbsPath = Environment.getExternalStorageDirectory().getPath() + File.separator +DOMAIN+File.separator+ CACHE_FLOD_NAME + File.separator;
        } else {
            fileAbsPath = BaseApplication.getInstance().getFilesDir().getPath() + File.separator + CACHE_FLOD_NAME + File.separator;
        }
        return fileAbsPath;
    }
}
