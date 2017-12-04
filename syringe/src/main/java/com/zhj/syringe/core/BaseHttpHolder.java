package com.zhj.syringe.core;

import com.zhj.syringe.core.attrs.ActionBindMap;
import com.zhj.syringe.core.attrs.RebindAttrAction;
import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.BaseHttpSubscriber;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.service.BaseServiceManager;
import com.zhj.syringe.utils.RxThreadUtils;
import com.zhj.syringe.utils.SyringeTransformer;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by Fred Zhao on 2017/2/28.
 */

public abstract class BaseHttpHolder {

    protected BaseServiceManager mBaseServiceManager;

    public BaseHttpHolder(BaseServiceManager baseServiceManager) {

        mBaseServiceManager = baseServiceManager;
    }

    public void post(BasePostBuilder builder) {

        post(builder.isSerial, new HolderObservableFormat(builder.mObservableFormat, builder),
                (BaseRequestParam[]) builder.mBaseRequestParams.toArray(new BaseRequestParam[ builder
                        .mBaseRequestParams.size() ]));
    }


    private void post(boolean isSerial, HolderObservableFormat observableFormat, BaseRequestParam...
            baseRequestParams) {

        final int length = baseRequestParams.length;
        if (length > 0) {
            observableFormat.beforePost();
            if (isSerial) method(0, observableFormat, baseRequestParams[ 0 ], baseRequestParams);
            else {
                ParallelRequestFinish parallelRequestFinish = new ParallelRequestFinish(length);
                for (int i = 0; i < length; i++)
                    method(i, observableFormat, baseRequestParams[ i ], parallelRequestFinish);

            }
        }
    }

    private void method(final int i, final ObservableFormat observableFormat, final BaseRequestParam
            baseRequestParam, final BaseRequestParam... baseRequestParams) {

        final int length = baseRequestParams.length;
        baseRequestParam.getObservable(mBaseServiceManager)
                .compose(RxThreadUtils.syringe(observableFormat, i, baseRequestParam.getHttpResponseFormat()))
                .subscribe(new SerialSubscriberProxy(baseRequestParam, observableFormat,
                        baseRequestParams, length, i));
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

        @Override
        public void onError(Throwable e) {

            super.onError(e);
            observableFormat.onError(e);
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
            else {
                observableFormat.beforeEnd();
            }
            super.onNext(httpBean);
            if (length <= i + 1) observableFormat.postComplete();
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


            parallelRequestFinish.requestFinish();
            if (parallelRequestFinish.isLast()) observableFormat.beforeEnd();
            super.onNext(httpBean);
            if (parallelRequestFinish.isLast()) observableFormat.postComplete();
        }
    }

    private void method(int i, final ObservableFormat observableFormat, final BaseRequestParam baseRequestParam, final
    ParallelRequestFinish parallelRequestFinish) {

        baseRequestParam.getObservable(mBaseServiceManager)
                .compose(RxThreadUtils.syringe(observableFormat, i, baseRequestParam.getHttpResponseFormat()))
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

        private boolean running;

        private HolderQueue holderQueue;

        private ObservableFormat defaultObservableFormat;

        public BasePostBuilder(BaseHttpHolder holder) {

            this.holder = holder;
        }

        /**
         * the requests post one by one
         *
         * @return
         */
        public synchronized T serial() {

            if (running) getHolderQueue().order(true);
            else isSerial = true;
            return (T) this;
        }

        /**
         * (default)
         * all the request post together
         *
         * @return
         */
        public synchronized T parallel() {

            if (running) getHolderQueue().order(false);
            else this.isSerial = false;
            return (T) this;
        }

        /**
         * Add some matter on the http observable,
         * and set the matter beforePost、beforeEnd、postComplete
         *
         * @param observableFormat
         * @return
         */
        public synchronized T observableFormat(ObservableFormat observableFormat) {

            if (running) getHolderQueue().addObservableFormat(observableFormat);
            else mObservableFormat = observableFormat;
            return (T) this;
        }

        /**
         * Add a defaultObservableFormat for this holder.
         * But the observableFormat has a higher priority.
         *
         * @param observableFormat
         * @return
         */
        public synchronized T defaultObservableFormat(ObservableFormat observableFormat) {

            this.defaultObservableFormat = observableFormat;
            return (T) this;
        }

        /**
         * Add a http request
         *
         * @param baseRequestParam
         * @return
         */
        public synchronized T addRequest(BaseRequestParam baseRequestParam) {

            if (running) getHolderQueue().addRequestParam(baseRequestParam);
            else {
                if (null == mBaseRequestParams) mBaseRequestParams = new LinkedHashSet<>();
                mBaseRequestParams.add(baseRequestParam);
            }
            return (T) this;
        }

        public final <B> B avoidSetter(int tag, B a, B b) {

            if (running) {
                getHolderQueue().addAttrs(tag, a);
                return b;
            } else return a;
        }

        void reBindAttrs(int tag, Object o) {

            RebindAttrAction action = ActionBindMap.getAction(tag, this);
            if (null != action) action.call(o);
        }


        private HolderQueue getHolderQueue() {

            if (null == this.holderQueue) this.holderQueue = new HolderQueue();
            return this.holderQueue;
        }

        public synchronized void post() {

            if (null == mObservableFormat) observableFormat(null == defaultObservableFormat ? DefaultConfigHolder
                    .getInstance().baseObservableFormat : defaultObservableFormat);
            if (null == mBaseRequestParams) return;
            if (running) getHolderQueue().post();
            else if (null != holder) {
                holder.post(this);
                running = true;
            }
        }

        public final T compose(SyringeTransformer<T> transformer) {

            return transformer.call((T) this);
        }

        synchronized void clear() {

            resetBuilder();
            getHolderQueue().push((T) this);
        }

        public final void resetBuilder() {

            mObservableFormat = null;
            if (null != mBaseRequestParams) {
                for (BaseRequestParam request : mBaseRequestParams) request.clear();
                mBaseRequestParams = null;
            }
            running = false;
            cleanAttr();
        }

        public final void resetQueue() {

            if (null != this.holderQueue) this.holderQueue.clear();
        }

        /**
         * clear the custom attr
         * if don't the attr will extended by next post
         */
        public void cleanAttr() {

        }
    }

}
