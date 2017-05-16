package com.zhj.example.service;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import syringe.DefaultPost;
import syringe.Service;
/**
 * Created by Fred Zhao on 2017/3/2.
 */
@Service
public interface IService {

    public static final String ISERVICE_NAME = "iService";

    @DefaultPost
    @GET(ISERVICE_NAME + "/get")
    Observable<String> get();

    @DefaultPost
    @POST(ISERVICE_NAME + "/post")
    Observable<String> post();

    @GET(ISERVICE_NAME + "/query")
    Observable<String> query(@Query("query") String query);

    @GET(ISERVICE_NAME + "/querymap")
    Observable<String> queryMap(@QueryMap Map<String, String> queryMap);

    @POST(ISERVICE_NAME + "/field")
    @FormUrlEncoded
    Observable<String> field(@Field("field") String field);

    @POST(ISERVICE_NAME + "/fieldmap")
    @FormUrlEncoded
    Observable<String> fieldMap(@FieldMap Map<String, String> fieldMap);


    @POST(ISERVICE_NAME + "/path/{pathone}")
    Observable<String> path(@Path("pathone") String one);

    @POST(ISERVICE_NAME + "/path/{pathone}/{pathtwo}")
    Observable<String> paths(@Path("pathone") String one, @Path("pathtwo") String two);

}
