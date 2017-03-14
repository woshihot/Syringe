package com.zhj.syringe.core.request;
import com.zhj.syringe.core.response.HttpBean;
/**
 * Created by Fred Zhao on 2016/12/21.
 */

public interface CascadeParamInterface {

    BaseRequestParam getCascadeParam(BaseRequestParam defaultParam, HttpBean bean);

    class DefaultCascadeParamImpl implements CascadeParamInterface {

        @Override
        public BaseRequestParam getCascadeParam(BaseRequestParam defaultParam, HttpBean bean) {

            return defaultParam;
        }
    }
}
