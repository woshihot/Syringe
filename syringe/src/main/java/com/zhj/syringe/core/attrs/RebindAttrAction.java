package com.zhj.syringe.core.attrs;
import com.zhj.syringe.core.BaseHttpHolder;

import rx.functions.Action1;
/**
 * Created by Fred Zhao on 2017/5/5.
 */

public abstract class RebindAttrAction<B extends BaseHttpHolder.BasePostBuilder> implements Action1 {

    protected B mBuilder;

    public RebindAttrAction(B builder) {

        mBuilder = builder;
    }


}
