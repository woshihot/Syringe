package com.zhj.syringe.core;
import com.zhj.syringe.core.request.ObservableFormat;

import rx.Observable;
/**
 * Created by Fred Zhao on 2017/5/3.
 */

public class HolderObservableFormat implements ObservableFormat {

    private ObservableFormat mObservableFormat;

    private BaseHttpHolder.BasePostBuilder mBuilder;

    public HolderObservableFormat(ObservableFormat observableFormat, BaseHttpHolder.BasePostBuilder builder) {

        mObservableFormat = observableFormat;
        mBuilder = builder;
    }

    @Override
    public Observable format(Observable observable, int position) {

        return mObservableFormat.format(observable, position);
    }

    @Override
    public void beforePost() {

        mObservableFormat.beforePost();
    }

    @Override
    public void beforeEnd() {

        mObservableFormat.beforeEnd();

    }

    @Override
    public void postComplete() {

        mObservableFormat.postComplete();
        mBuilder.clear();

    }
}
