package com.zhj.syringe.core.request;

import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpResponseFormat;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseRequestParamBuilder<T extends BaseRequestParamBuilder> {

    protected String url;

    protected Map<String, Object> param;

    protected Map<String, String> headers;

    protected Map<String, Object> paths;

    //客户端处理
    protected BaseHttpSubscriber mHttpSubscriber;

    //配置下一项的请求
    protected CascadeParamInterface mCascadeParamInterface;

    //string转bean规则
    protected HttpResponseFormat mHttpResponseFormat;

    //配置上传参数规则
    protected HttpRequestFormat mHttpRequestFormat;


    String getUrl() {

        return url;
    }

    Map<String, Object> getParam() {

        return param;
    }

    BaseHttpSubscriber getHttpSubscriber() {

        return mHttpSubscriber;
    }

    CascadeParamInterface getCascadeParamInterface() {

        return mCascadeParamInterface;
    }

    HttpResponseFormat getHttpResponseFormat() {

        return mHttpResponseFormat;
    }

    HttpRequestFormat getHttpRequestFormat() {

        return mHttpRequestFormat;
    }

    Map getHeaders() {

        return headers;
    }

    Map getPaths() {

        return paths;
    }

    public T paths(Map paths) {

        this.paths = paths;
        return (T) this;
    }

    public T url(String url) {

        this.url = url;
        return (T) this;
    }

    public T param(Map param) {

        this.param = param;
        return (T) this;
    }

    public T headers(Map headers) {

        this.headers = headers;
        return (T) this;
    }

    public T header(String key, String value) {

        if (null == this.headers) this.headers = new HashMap<>();
        this.headers.put(key, value);
        return (T) this;
    }

    public T path(String key, String value) {

        if (null == this.paths) this.paths = new HashMap<>();
        this.paths.put(key, value);
        return (T) this;

    }

    public T subscriber(BaseHttpSubscriber httpSubscriber) {

        this.mHttpSubscriber = httpSubscriber;
        return (T) this;
    }

    public T cascade(CascadeParamInterface cascadeParamInterface) {

        this.mCascadeParamInterface = cascadeParamInterface;
        return (T) this;
    }

    public T responseFormat(HttpResponseFormat httpResponseFormat) {

        this.mHttpResponseFormat = httpResponseFormat;
        return (T) this;
    }

    public T requestFormat(HttpRequestFormat httpRequestFormat) {

        this.mHttpRequestFormat = httpRequestFormat;
        return (T) this;
    }

    public abstract BaseRequestParam build();

}
