package com.zhj.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zhj.example.service.IServiceType;
import com.zhj.example.service.RequestParam;
import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.CascadeParamInterface;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.request.ObservableFormat;
import com.zhj.syringe.core.response.HttpBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, Object> obj = new HashMap<>();
        obj.put("cmd", "support/app/version/current");
        Map<String, Object> param = new HashMap<>();
        param.put("appCode", "isesol-rentPlatform");
        param.put("channel", "");
        param.put("appType", "android");
        obj.put("parameters", param);

        String path1 = "/storage/emulated/0/Pictures/IMG_20161017_135420.jpg";
        Map<String, Object> fileObj = new HashMap<>();
        fileObj.put("iToken", "aa7c7873-0ff4-4de1-966f-bd01960810c1");
        fileObj.put("file", new File(path1));
        new HttpHolder.PostBuilder(Client.getInstance().getHttpHolder())
                .parallel()
                .observableFormat(new ObservableFormat() {

                    @Override
                    public Observable format(Observable observable, int position) {

                        return observable;
                    }

                    @Override
                    public void beforePost() {

                        Log.d("MainActivity", "loading");
                    }

                    @Override
                    public void beforeEnd() {

                        Log.d("MainActivity", "end loading");
                    }
                })
                .addRequest(RequestParam.newBuilder().param(obj).cascade(new CascadeParamInterface() {

                    @Override
                    public BaseRequestParam getCascadeParam(BaseRequestParam defaultParam, HttpBean bean) {
                        return defaultParam;
                    }
                }).build())
                .addRequest(RequestParam.newBuilder().param(fileObj).requestFormat(new HttpRequestFormat() {

                    @Override
                    public Object formatApiParam(Map param) {

                        return configFileParam(param);
                    }
                }).service(IServiceType.CLASS).upload().build())
                .post();
    }

    private List<MultipartBody.Part> configFileParam(Map<String, Object> param) {

        List<MultipartBody.Part> parts = new ArrayList<>();
        for (Map.Entry<String, Object> paramEntry : param.entrySet()) {
            if (paramEntry.getValue() instanceof File) {
                File file = (File) paramEntry.getValue();
                parts.add(MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType
                        .parse(MimeTypeHelper.getMimeTypeForExtension(file.getName())), file)));
            } else {
                parts.add(MultipartBody.Part.createFormData(paramEntry.getKey(), String.valueOf(paramEntry
                        .getValue()
                )));
            }
        }

        return parts;
    }
}
