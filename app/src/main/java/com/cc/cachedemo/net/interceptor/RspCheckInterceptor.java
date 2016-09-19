package com.cc.cachedemo.net.interceptor;

import com.cc.cachedemo.utils.InterceptorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by cc on 2016/9/18.
 */
public class RspCheckInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        try {
            ResponseBody rspBody = response.body();
            JSONObject jsonObject = new JSONObject(InterceptorUtils.getRspData(rspBody));
            int status = jsonObject.getInt("status");
            if (status < 200 || status >= 300){
                throw new IOException(jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("parase data error");
        }catch (Exception e){
            if (e instanceof IOException){
                throw (IOException)e;
            }
        }

        return response;
    }
}
