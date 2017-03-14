package com.zhj.syringe.core.service;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseServiceManager {


    public abstract <T> T getService(final Class<T> service);

}
