package com.zhj.example;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zhj.syringe.core.BaseHttpHolderConfiguration;
import com.zhj.syringe.core.DefaultConfigHolder;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.response.HttpResponseFormat;

import java.util.Map;

import syringe.HttpHolderConfig;
/**
 * Created by Fred Zhao on 2017/3/6.
 */
@HttpHolderConfig
public class HolderConfig extends BaseHttpHolderConfiguration {

    @Override
    public HttpRequestFormat configDefaultHttpRequest() {

        return new HttpRequestFormat() {

            @Override
            public Object formatApiParam(Map param) {
                Log.d("HolderConfig&Post", JSON.toJSONString(param));
                return param;
            }
        };
    }

    @Override
    public HttpResponseFormat configDefaultHttpResponse() {

        return DefaultConfigHolder.getInstance().baseHttpResponseFormat;
    }

    @Override
    public BaseHttpSubscriber configDefaultSubscriber() {

        return new BaseHttpSubscriber() {

            @Override
            public void onNext(HttpBean httpBean) {

                Log.d("HolderConfig", JSON.toJSONString(httpBean));
            }
        };
    }
}
