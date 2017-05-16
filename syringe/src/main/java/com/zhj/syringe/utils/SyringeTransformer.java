package com.zhj.syringe.utils;
import com.zhj.syringe.core.BaseHttpHolder;
/**
 * Created by Fred Zhao on 2017/5/9.
 */

public interface SyringeTransformer<T extends BaseHttpHolder.BasePostBuilder> {

    T call(T t);

}
