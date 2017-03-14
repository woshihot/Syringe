package com.zhj.syringe.core.request;
import java.util.Map;
/**
 * Created by Fred Zhao on 2017/2/27.
 */

public interface HttpRequestFormat<T> {

    T formatApiParam(Map<String, Object> param);
}
