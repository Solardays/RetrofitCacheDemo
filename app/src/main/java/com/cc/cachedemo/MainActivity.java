package com.cc.cachedemo;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.cachedemo.api.ApiService;
import com.cc.cachedemo.entries.AliAddrsBean;
import com.cc.cachedemo.net.RetrofitManager;
import com.cc.cachedemo.utils.LogUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView locTv;
    private ImageView imgRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initId();
        initData();
    }

    public void test(View view){    }

    private void initData() {
        ApiService apiService = RetrofitManager.getInstance().createReq(ApiService.class);
        Map map = new ArrayMap<String,String>();
        map.put("a","上海市");
        map.put("aa","松江区");
        map.put("aaa","车墩镇");
        apiService.getIndexContentOne().enqueue(new Callback<AliAddrsBean>() {
            @Override
            public void onResponse(Call<AliAddrsBean> call, Response<AliAddrsBean> response) {
                AliAddrsBean aliAddrsBean = response.body();
                LogUtil.e("onResponse");
                locTv.setText("经度："+aliAddrsBean.lon+"\n维度："+aliAddrsBean.lat);
            }

            @Override
            public void onFailure(Call<AliAddrsBean> call, Throwable t) {
                LogUtil.e("onFailure");
            }
        });

//        apiService.getIndexContentTwo().subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.newThread()).subscribe(new Action1<AliAddrsBean>() {
//            @Override
//            public void call(AliAddrsBean aliAddrsBean) {
//                locTv.setText("经度："+aliAddrsBean.lon+"\n维度："+aliAddrsBean.lat);
//            }
//        });


    }

    private void initId() {
        locTv = (TextView) findViewById(R.id.tv_loc);
        imgRv = (ImageView) findViewById(R.id.rv_img);
    }
}
