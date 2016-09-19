package com.cc.cachedemo.net.interceptor;

import com.cc.cachedemo.BaseApplication;
import com.cc.cachedemo.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cc on 2016/9/18.
 */
public class RspCacheControllerInterceptor implements Interceptor {
    private final int maxAge = 60 * 60 * 24 *7;
    private final int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // Add Cache Control only for GET methods

        if (request.method().equals("GET")) {
            Response originalResponse = chain.proceed(chain.request());

            if (NetworkUtils.isNetwork(BaseApplication.getInstance())) {

                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {

                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }

        Response originalResponse = chain.proceed(request);
        return originalResponse;
    }
}
