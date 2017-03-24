package com.zhj.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zhj.example.service.IServiceType;
import com.zhj.example.service.RequestParam;
import com.zhj.syringe.core.request.HttpRequestFormat;
import com.zhj.syringe.core.request.ObservableFormat;

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

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("query", "zero");
        queryMap.put("queryOne", "one");
        queryMap.put("queryTwo", "two");

        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("field", "zero");
        fieldMap.put("fieldOne", "one");
        fieldMap.put("fieldTwo", "two");
        new HttpHolder.PostBuilder(Client.getInstance().getHttpHolder())
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
                .addRequest(RequestParam.newBuilder().service(IServiceType.CLASS).post().build())
                .addRequest(RequestParam.newBuilder().service(IServiceType.CLASS).get().build())
                .addRequest(RequestParam.newBuilder().param(queryMap).requestFormat(new HttpRequestFormat() {

                    @Override
                    public Object formatApiParam(Map param) {

                        return param.get("query");
                    }
                }).service(IServiceType.CLASS).query().build())
                .addRequest(RequestParam.newBuilder().param(queryMap).service(IServiceType.CLASS).queryMap().build())
                .addRequest(RequestParam.newBuilder().param(fieldMap).requestFormat(new HttpRequestFormat() {

                    @Override
                    public Object formatApiParam(Map param) {

                        return param.get("field");
                    }
                }).service(IServiceType.CLASS)
                        .field().build())
                .addRequest(RequestParam.newBuilder().param(fieldMap).service(IServiceType.CLASS).fieldMap().build())
                .addRequest(RequestParam.newBuilder().path("pathone", "pathone").service(IServiceType.CLASS).path()
                        .build())
                .addRequest(RequestParam.newBuilder().path("pathone", "pathone").path("pathtwo", "pathtwo").service
                        (IServiceType.CLASS).paths().build())
                .serial().post();
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
