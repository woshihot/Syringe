package com.zhj.example.service;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import syringe.Service;
/**
 * Created by Fred Zhao on 2017/3/1.
 */
@Service
public interface TService {
    public static final String GET_RESP="https://api.github.com/repos/woshihot/Syringe";

    @GET
    Observable<String> getStarCount(@Url String url);
}
