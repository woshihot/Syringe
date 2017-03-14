package com.zhj.example.service;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import syringe.Service;
/**
 * Created by Fred Zhao on 2017/3/1.
 */
@Service
public interface TService {
    @POST("cgi")
    @Headers("Content-Type:application/json; charset=UTF-8")
    Observable<String> callt(@Body String str);

    @Multipart
    Observable<String> uploadt(@Part List<MultipartBody.Part> files);
}
