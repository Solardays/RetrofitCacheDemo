package com.cc.cachedemo.api;

import com.cc.cachedemo.entries.AliAddrsBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by cc on 2016/9/18.
 */
public interface ApiService {

    @Headers("Cache-Control:max-age=3600")
    @GET("http://gc.ditu.aliyun.com/geocoding?a=上海市&aa=松江区&aaa=车墩镇")
    Call<AliAddrsBean> getIndexContentOne();

    @GET("http://gc.ditu.aliyun.com/geocoding?a=上海市&aa=松江区&aaa=车墩镇")
    Observable<AliAddrsBean> getIndexContentTwo();

    @POST("geocoding?")
    Call<AliAddrsBean> getIndexContentThree(
            @QueryMap Map<String, Object> map
    );


}
