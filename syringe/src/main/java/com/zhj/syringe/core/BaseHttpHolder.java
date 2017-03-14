package com.zhj.syringe.core;

import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.service.BaseServiceManager;

import java.util.HashSet;
import java.util.LinkedHashSet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
        final Observable observable = baseRequestParam.getObservable(mBaseServiceManager);
        observableFormat.format(observable.subscribeOn(Schedulers.io()), i).subscribeOn(AndroidSchedulers.mainThread
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {

            @Override
            public void call(Object s) {

                HttpBean backBean = baseRequestParam.getHttpResponseFormat().formatHttpBean(s);
                if (length > i + 1) {
                    method(i + 1, observableFormat, baseRequestParam.getCascadeParamInterface().getCascadeParam
                            (baseRequestParams[ i + 1 ], backBean), baseRequestParams);
                } else {
                    observableFormat.beforeEnd();
                }
                baseRequestParam.getHttpSubscriber().onNext(backBean);
            }
        });
    }

    private void method(int i, final ObservableFormat observableFormat, final BaseRequestParam baseRequestParam, final
    ParallelRequestFinish
            parallelRequestFinish) {

        observableFormat.format(baseRequestParam.getObservable(mBaseServiceManager).subscribeOn(Schedulers.io())
                , i).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (new Action1<Object>() {

                    @Override
                    public void call(Object s) {

                        HttpBean backBean = baseRequestParam.getHttpResponseFormat().formatHttpBean(s);
                        parallelRequestFinish.requestFinish();
                        if (parallelRequestFinish.isLast()) observableFormat.beforeEnd();
                        baseRequestParam.getHttpSubscriber().onNext(backBean);

                    }
                });
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
