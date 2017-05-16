package com.zhj.syringe.utils;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.HttpBean;
import com.zhj.syringe.core.response.HttpResponseFormat;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * Created by Fro on 2017/5/3.
 */

public class RxThreadUtils {

    private static MainThreadTransformer sMainThreadTransformer;

    private static NewThreadTransformer sNewThreadTransformer;

    public static MainThreadTransformer withMainThread() {

        if (null == sMainThreadTransformer) sMainThreadTransformer = new MainThreadTransformer();
        return sMainThreadTransformer;
    }

    public static NewThreadTransformer beforeNewThread() {

        if (null == sNewThreadTransformer) sNewThreadTransformer = new NewThreadTransformer();
        return sNewThreadTransformer;
    }

    public static SyringeTransformer syringe(ObservableFormat observableFormat, int i, HttpResponseFormat
            httpResponseFormat) {

        return new SyringeTransformer(new FormatTransformer(observableFormat, i), httpResponseFormat);
    }

    static class MainThreadTransformer implements Observable.Transformer<Object, Object> {

        @Override
        public Observable call(Observable objectObservable) {

            return objectObservable.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers
                    .mainThread());
        }
    }

    static class NewThreadTransformer implements Observable.Transformer<Object, Object> {

        @Override
        public Observable call(Observable objectObservable) {

            return objectObservable.subscribeOn(Schedulers.newThread());
        }
    }


    static class FormatTransformer implements Observable.Transformer<Object, Object> {

        private ObservableFormat mObservableFormat;

        private int i;

        public FormatTransformer(ObservableFormat observableFormat, int i) {

            mObservableFormat = observableFormat;
            this.i = i;
        }

        @Override
        public Observable call(Observable objectObservable) {

            return mObservableFormat.format(objectObservable, i);
        }
    }


    static class SyringeTransformer implements Observable.Transformer<Object, Object> {

        private FormatTransformer mFormatTransformer;

        private HttpResponseFormat mHttpResponseFormat;

        public SyringeTransformer(FormatTransformer formatTransformer, HttpResponseFormat httpResponseFormat) {

            mFormatTransformer = formatTransformer;
            mHttpResponseFormat = httpResponseFormat;
        }

        @Override
        public Observable call(Observable objectObservable) {

            return objectObservable
                    .compose(beforeNewThread())
                    .compose(mFormatTransformer)
                    .compose(withMainThread())
                    .map(new BaseFormatFunc1(mHttpResponseFormat));
        }
    }

    static class BaseFormatFunc1 implements Func1<Object, HttpBean> {

        private HttpResponseFormat httpResponseFormat;

        public BaseFormatFunc1(HttpResponseFormat httpResponseFormat) {

            this.httpResponseFormat = httpResponseFormat;
        }

        @Override
        public HttpBean call(Object o) {

            return httpResponseFormat.formatHttpBean(o);
        }
    }
}
