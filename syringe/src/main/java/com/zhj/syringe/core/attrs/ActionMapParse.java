package com.zhj.syringe.core.attrs;
import com.zhj.syringe.core.BaseHttpHolder;
/**
 * Created by Fred Zhao on 2017/5/8.
 */

public interface ActionMapParse {

    RebindAttrAction parseAction(int tag, BaseHttpHolder.BasePostBuilder builder);
}
