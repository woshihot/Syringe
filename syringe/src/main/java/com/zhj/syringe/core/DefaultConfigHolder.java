package com.zhj.syringe.core;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.response.HttpResponseFormat;

import java.util.Map;

import rx.Observable;
/**
 * Created by Fred Zhao on 2017/3/7.
 */

public class DefaultConfigHolder {

    public HttpRequestFormat baseHttpRequestFormat;

    public HttpResponseFormat baseHttpResponseFormat;

    public BaseHttpSubscriber baseHttpSubscriber;

    public ObservableFormat baseObservableFormat;

    private static DefaultConfigHolder instance;

    public static synchronized DefaultConfigHolder getInstance() {

        if (null == instance) instance = new DefaultConfigHolder();
        return instance;
    }

    public DefaultConfigHolder() {

        baseHttpRequestFormat = new BaseHttpRequestFormat();
        baseHttpResponseFormat = new BaseHttpResponseFormat();
        baseHttpSubscriber = new BaseHttpSubscriber() {

            @Override
            public void onNext(HttpBean httpBean) {

            }
        };
        baseObservableFormat = new BaseObservableFormat();
    }

    class BaseHttpRequestFormat implements HttpRequestFormat {

        @Override
        public Object formatApiParam(Map param) {

            return param;
        }
    }

    class BaseHttpResponseFormat implements HttpResponseFormat {

        @Override
        public HttpBean formatHttpBean(Object resp) {

            HttpBean httpBean = new HttpBean();
            httpBean.setMessage(resp);
            return httpBean;
        }
    }

    class BaseObservableFormat implements ObservableFormat {

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
    }

}
