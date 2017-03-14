package com.zhj.syringe.core;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpResponseFormat;
/**
 * Created by Fred Zhao on 2017/3/5.
 */

public abstract class BaseHttpHolderConfiguration<T extends BaseHttpHolderConfiguration> {

    public abstract HttpRequestFormat configDefaultHttpRequest();

    public abstract HttpResponseFormat configDefaultHttpResponse();

    public abstract BaseHttpSubscriber configDefaultSubscriber();

}
