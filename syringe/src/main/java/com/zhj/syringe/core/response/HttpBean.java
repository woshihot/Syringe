package com.zhj.syringe.core.response;
import static com.zhj.syringe.utils.Preconditions.checkArgument;
/**
 * Created by Fred Zhao on 2017/2/27.
 */

public class HttpBean {

    private boolean isSuccess = true;//是否正确返回

    private Object message;//失败返回的error信息

    private String content;//成功返回后的信息

    private String extralInfo;//扩展信息


    public boolean isSuccess() {

        return isSuccess;
    }

    public void setSuccess(boolean success) {

        isSuccess = success;
    }

    public Object getMessage() {

        return message;
    }

    public void setMessage(Object message) {

        this.message = message;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getExtralInfo() {

        return extralInfo;
    }

    public void setExtralInfo(String extralInfo) {

        this.extralInfo = extralInfo;
    }

    public static class DefaultErrorHttpBean extends HttpBean {

        public DefaultErrorHttpBean(Object msg) {

            checkArgument(msg instanceof String || msg instanceof Integer, "msg must be string resourse" +
                    " " + "or string");
            setSuccess(false);
            setMessage(msg);
        }
    }
}
