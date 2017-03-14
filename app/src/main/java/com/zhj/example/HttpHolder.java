package com.zhj.example;
import com.zhj.example.service.ServiceManager;
import com.zhj.syringe.core.BaseHttpHolder;
import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.ObservableFormat;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public class HttpHolder extends BaseHttpHolder {

    public HttpHolder(ServiceManager serviceManager) {

        super(serviceManager);
    }

    @Override
    public void post(boolean isSerial, ObservableFormat observableFormat, BaseRequestParam... baseRequestParams) {
        super.post(isSerial, observableFormat, baseRequestParams);
    }

    public static class PostBuilder extends BasePostBuilder<PostBuilder> {

        public PostBuilder(BaseHttpHolder holder) {

            super(holder);
        }

        @Override
        public void post() {
            /*
            * you can do something in here to judge if post
            * just like network check、set default ObservableFormat
            * */
            super.post();

        }
    }
}
