package com.zhj.syringe.core.response;
import rx.Subscriber;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseHttpSubscriber extends Subscriber<HttpBean> {

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {

        onNext(new HttpBean.DefaultErrorHttpBean(e.getMessage()));
    }

}
