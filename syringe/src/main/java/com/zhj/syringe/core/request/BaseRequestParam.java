package com.zhj.syringe.core.request;

import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpResponseFormat;
import com.zhj.syringe.core.service.BaseServiceManager;

import java.util.Map;

import rx.Observable;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseRequestParam {

    private BaseRequestParamBuilder mBuilder;

    public BaseRequestParam(BaseRequestParamBuilder builder) {

        this.mBuilder = builder;
        this.url = builder.getUrl();
        this.param = builder.getParam();
        this.mHttpSubscriber = builder.getHttpSubscriber();
        this.mCascadeParamInterface = builder.getCascadeParamInterface();
        this.mHttpResponseFormat = builder.getHttpResponseFormat();
        this.mHttpRequestFormat = builder.getHttpRequestFormat();
        this.headers = builder.getHeaders();
        this.paths = builder.getPaths();
    }

    public BaseRequestParamBuilder getBuilder() {

        return mBuilder;
    }

    private String url;

    private Map<String, Object> param;

    private Map<String, String> headers;

    private Map<String, Object> paths;


    //客户端处理
    protected BaseHttpSubscriber mHttpSubscriber;

    //配置下一项的请求
    private CascadeParamInterface mCascadeParamInterface;

    //string转bean规则
    private HttpResponseFormat mHttpResponseFormat;

    //配置上传参数规则
    private HttpRequestFormat mHttpRequestFormat;


    public Map getParam() {

        return param;
    }

    public CascadeParamInterface getCascadeParamInterface() {

        return mCascadeParamInterface;
    }

    public HttpResponseFormat getHttpResponseFormat() {

        return mHttpResponseFormat;
    }

    public BaseHttpSubscriber getHttpSubscriber() {

        return mHttpSubscriber;
    }

    public HttpRequestFormat getHttpRequestFormat() {

        return mHttpRequestFormat;
    }

    public String getUrl() {

        return url;
    }

    public Map getHeaders() {

        return headers;
    }

    public Map getPaths() {

        return paths;
    }

    public abstract Observable getObservable(BaseServiceManager baseServiceManager);


}
