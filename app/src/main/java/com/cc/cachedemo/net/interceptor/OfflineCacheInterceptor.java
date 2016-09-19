package com.cc.cachedemo.net.interceptor;

import com.cc.cachedemo.BaseApplication;
import com.cc.cachedemo.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cc on 2016/9/18.
 */
public class OfflineCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        /**
         * 未联网获取缓存数据
         */
        if (!NetworkUtils.isNetwork(BaseApplication.getInstance())) {
            //在20秒缓存有效，此处测试用，实际根据需求设置具体缓存有效时间
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(20, TimeUnit.SECONDS)
                    .build();

            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
        }

        return chain.proceed(request);
    }
}
