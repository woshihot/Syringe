package com.zhj.syringe.core;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpResponseFormat;
/**
 * Created by Fred Zhao on 2017/3/5.
 */

public abstract class BaseHttpHolderConfiguration<T extends BaseHttpHolderConfiguration> {

    /**
     * format the map to requestParam
     *
     * @return
     */
    public abstract HttpRequestFormat configDefaultHttpRequest();

    /**
     * format the response object to httpbean
     *
     * @return
     */
    public abstract HttpResponseFormat configDefaultHttpResponse();

    /**
     * if the request needn't subscriber on the sponsor
     * setter a default subscriber
     *
     * @return
     */
    public abstract BaseHttpSubscriber configDefaultSubscriber();

}
