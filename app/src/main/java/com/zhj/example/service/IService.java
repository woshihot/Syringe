package com.zhj.example.service;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;
import syringe.DefaultPost;
import syringe.Service;
/**
 * Created by Fred Zhao on 2017/3/2.
 */
@Service
public interface IService {

    @POST("cgi")
    @DefaultPost
    @Headers("Content-Type:application/json; charset=UTF-8")
    Observable<String> call(@Query("testQuery") String query, @FieldMap Map fieldMap, @Part("testField") String
            field);

    @Multipart
    @POST("https://oncallservice.i5sesol.com/file/fileUpload")
    Observable<String> upload(@Part List<MultipartBody.Part> files);
}
