package com.zhj.example;
import com.zhj.example.service.ServiceManager;
import com.zhj.syringe.core.BaseHttpHolder;
/**
 * Created by Fred Zhao on 2017/2/28.
 */

public class HttpHolder extends BaseHttpHolder {

    public HttpHolder(ServiceManager serviceManager) {

        super(serviceManager);
    }

    public static class PostBuilder extends BasePostBuilder<PostBuilder> {

//        @BindAttr
        private String mNoInternetAction;


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

        public PostBuilder noInternetAction(String noInternetAction) {

//            this.mNoInternetAction = avoidSetter(BA.mNoInternetAction, noInternetAction, this.mNoInternetAction);
            return this;
        }

        public String getNoInternetAction() {

            return mNoInternetAction;
        }
    }

}
