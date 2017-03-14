package com.zhj.syringe.core.request;
import rx.Observable;

/**
 * Created by Fred Zhao on 2017/2/28.
 */

public interface ObservableFormat {

    Observable format(Observable observable, int position);

    void beforePost();

    void beforeEnd();
}
