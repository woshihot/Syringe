package com.zhj.syringe.core;

import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.response.HttpResponseFormat;
import com.zhj.syringe.core.service.BaseServiceManager;

import java.util.HashSet;
import java.util.LinkedHashSet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseHttpHolder {

    protected BaseServiceManager mBaseServiceManager;

    public BaseHttpHolder(BaseServiceManager baseServiceManager) {

        mBaseServiceManager = baseServiceManager;
    }

    public void post(boolean isSerial, ObservableFormat observableFormat, BaseRequestParam... baseRequestParams) {

        final int length = baseRequestParams.length;
        if (length > 0) {
            observableFormat.beforePost();
            if (isSerial) method(0, observableFormat, baseRequestParams[ 0 ], baseRequestParams);
            else {
                ParallelRequestFinish parallelRequestFinish = new ParallelRequestFinish(length);
                for (int i = 0; i < length; i++) {
                    method(i, observableFormat, baseRequestParams[ i ], parallelRequestFinish);
                }
            }
        }
    }

    private void method(final int i, final ObservableFormat observableFormat, final BaseRequestParam
            baseRequestParam, final BaseRequestParam... baseRequestParams) {

        final int length = baseRequestParams.length;
        Observable observable = baseRequestParam.getObservable(mBaseServiceManager);
        observableFormat.format(observable.subscribeOn(Schedulers.newThread()), i).subscribeOn(AndroidSchedulers
                .mainThread()).observeOn(AndroidSchedulers.mainThread()).map(new BaseFormatFunc1(baseRequestParam
                .getHttpResponseFormat())).subscribe(new SerialSubscriberProxy(baseRequestParam, observableFormat,
                baseRequestParams, length, i));
    }

    class BaseFormatFunc1 implements Func1<Object, HttpBean> {

        private HttpResponseFormat httpResponseFormat;

        public BaseFormatFunc1(HttpResponseFormat httpResponseFormat) {

            this.httpResponseFormat = httpResponseFormat;
        }

        @Override
        public HttpBean call(Object o) {

            return httpResponseFormat.formatHttpBean(o);
        }
    }

    class InsteadSubscriberProxy extends BaseHttpSubscriber {

        BaseRequestParam contentRequestParam;

        ObservableFormat observableFormat;

        public InsteadSubscriberProxy(BaseRequestParam contentRequestParam, ObservableFormat observableFormat) {

            this.contentRequestParam = contentRequestParam;
            this.observableFormat = observableFormat;
        }

        @Override
        public void onNext(HttpBean httpBean) {

            contentRequestParam.getHttpSubscriber().onNext(httpBean);
        }
    }

    class SerialSubscriberProxy extends InsteadSubscriberProxy {

        BaseRequestParam[] requestParams;

        int i;

        int length;

        public SerialSubscriberProxy(BaseRequestParam contentRequestParam, ObservableFormat observableFormat,
                                     BaseRequestParam[] requestParams, int length, int i) {

            super(contentRequestParam, observableFormat);
            this.requestParams = requestParams;
            this.i = i;
            this.length = length;
        }

        @Override
        public void onNext(HttpBean httpBean) {

            if (length > i + 1)
                method(i + 1, observableFormat, this.contentRequestParam.getCascadeParamInterface().getCascadeParam
                        (this.requestParams[ i + 1 ], httpBean), this.requestParams);
            else observableFormat.beforeEnd();

            super.onNext(httpBean);
        }
    }

    class ParallelSubscriberProxy extends InsteadSubscriberProxy {

        ParallelRequestFinish parallelRequestFinish;

        public ParallelSubscriberProxy(BaseRequestParam contentRequestParam, ObservableFormat observableFormat,
                                       ParallelRequestFinish parallelRequestFinish) {

            super(contentRequestParam, observableFormat);
            this.parallelRequestFinish = parallelRequestFinish;
        }

        @Override
        public void onNext(HttpBean httpBean) {

            super.onNext(httpBean);
            parallelRequestFinish.requestFinish();
            if (parallelRequestFinish.isLast()) observableFormat.beforeEnd();
        }
    }

    private void method(int i, final ObservableFormat observableFormat, final BaseRequestParam baseRequestParam, final
    ParallelRequestFinish parallelRequestFinish) {

        observableFormat.format(baseRequestParam.getObservable(mBaseServiceManager).subscribeOn(Schedulers.io())
                , i).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).map(new
                BaseFormatFunc1(baseRequestParam.getHttpResponseFormat()))
                .subscribe(new ParallelSubscriberProxy(baseRequestParam, observableFormat, parallelRequestFinish));
    }

    class ParallelRequestFinish {

        private int length;

        private int i = 0;

        public ParallelRequestFinish(int length) {

            this.length = length;
        }

        public void requestFinish() {

            i += 1;
        }

        public boolean isLast() {

            return i >= length;
        }
    }

    public static class BasePostBuilder<T extends BasePostBuilder> {

        private boolean isSerial;

        private ObservableFormat mObservableFormat;

        private HashSet<BaseRequestParam> mBaseRequestParams;

        private BaseHttpHolder holder;

        public BasePostBuilder(BaseHttpHolder holder) {

            this.holder = holder;
        }

        public T serial() {

            isSerial = true;
            return (T) this;
        }

        public T parallel() {

            this.isSerial = false;
            return (T) this;
        }

        public T observableFormat(ObservableFormat observableFormat) {

            mObservableFormat = observableFormat;
            return (T) this;
        }

        public T addRequest(BaseRequestParam baseRequestParam) {

            if (null == mBaseRequestParams) mBaseRequestParams = new LinkedHashSet<>();
            mBaseRequestParams.add(baseRequestParam);
            return (T) this;
        }

        public void post() {

            if (null == mObservableFormat) observableFormat(DefaultConfigHolder.getInstance().baseObservableFormat);
            if (null == mBaseRequestParams) return;
            if (null != holder) holder.post(this.isSerial, this.mObservableFormat, mBaseRequestParams.toArray(new
                    BaseRequestParam[ mBaseRequestParams.size() ]));
        }
    }

}
