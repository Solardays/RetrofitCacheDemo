package com.cc.cachedemo.net.interceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by cc on 2016/9/18.
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed( chain.request() );

        // re-write response header to force use of cache
        // 正常访问同一请求接口（多次访问同一接口），给30秒缓存，超过时间重新发送请求，否则取缓存数据
        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(30, TimeUnit.SECONDS )
                .build();

        return response.newBuilder()
                .header( "Cache-Control", cacheControl.toString() )
                .build();
    }
}
