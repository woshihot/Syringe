package com.zhj.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.zhj.example.service.IServiceType;
import com.zhj.example.service.RequestParam;
import com.zhj.example.service.RequestParamBuilder;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpBean;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;


public class MainActivity extends AppCompatActivity {

    private HttpHolder.PostBuilder holder;


    private Map<String, Object> queryMap;

    private Map<String, Object> fieldMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holder = new HttpHolder.PostBuilder(Client.getInstance().getHttpHolder());
        queryMap = new HashMap<>();
        queryMap.put("query", "zero");
        queryMap.put("queryOne", "one");
        queryMap.put("queryTwo", "two");

        fieldMap = new HashMap<>();
        fieldMap.put("field", "zero");
        fieldMap.put("fieldOne", "one");
        fieldMap.put("fieldTwo", "two");
        holder.defaultObservableFormat(new ObservableFormat() {

            @Override
            public Observable format(Observable observable, int position) {

                return observable;
            }

            @Override
            public void beforePost() {

            }

            @Override
            public void beforeEnd() {

            }

            @Override
            public void onError(Throwable e) {

                Log.d("MainActivity", "error : "+e.getMessage());
            }

            @Override
            public void postComplete() {

                Log.d("MainActivity", "postComplete");
            }
        });
    }


    public void post(View view) {
        holder.addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).post()).build()).noInternetAction
                ("post")
                .addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).post()).build()).noInternetAction
                ("post").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).post()).build()).noInternetAction
                ("post").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).post()).build()).noInternetAction
                ("post").addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").post();

    }

    public void get(View view) {

        holder.addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").post();
        holder.addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").post();
        holder.addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").post();
        holder.addRequest(lod(RequestParam.newBuilder().service(IServiceType.CLASS).get()).build()).noInternetAction
                ("get").post();

    }

    public void query(View view) {

        holder.addRequest(RequestParam.newBuilder().param(queryMap).requestFormat(new HttpRequestFormat() {

            @Override
            public Object formatApiParam(Map param) {

                return param.get("query");
            }
        }).service(IServiceType.CLASS).query().build()).post();

    }

    public void querymap(View view) {

        holder.addRequest(RequestParam.newBuilder().param(queryMap).service(IServiceType.CLASS).queryMap().build())
                .post();

    }

    public void field(View view) {

        holder.addRequest(RequestParam.newBuilder().param(fieldMap).requestFormat(new HttpRequestFormat() {

            @Override
            public Object formatApiParam(Map param) {

                return param.get("field");
            }
        }).service(IServiceType.CLASS).field().build()).post();

    }

    public void fieldmap(View view) {

        holder.addRequest(RequestParam.newBuilder().param(fieldMap).service(IServiceType.CLASS).fieldMap().build())
                .post();

    }

    public void path(View view) {

        holder.addRequest(lod(RequestParam.newBuilder().path("pathone", "pathone").service(IServiceType.CLASS).path())
                .build()).post();

    }

    public void paths(View view) {

        holder.addRequest(RequestParam.newBuilder().path("pathone", "pathone").path("pathtwo", "pathtwo").service
                (IServiceType.CLASS).paths().build()).post();
    }


    public RequestParamBuilder lod(RequestParamBuilder builder) {

        return builder.subscriber(new BaseHttpSubscriber() {

            @Override
            public void onNext(HttpBean httpBean) {

                Log.d("MainActivity", JSON.parseObject(httpBean.getMessage().toString()).getString("content") + holder
                        .getNoInternetAction());
            }
        });
    }


}
