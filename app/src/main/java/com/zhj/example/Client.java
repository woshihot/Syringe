package com.zhj.example;
import com.zhj.example.service.ServiceManager;
import com.zhj.example.service.TService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
/**
 * Created by Fred Zhao on 2017/3/14.
 */

public class Client {

    public static final String BASE_URL = "https://private-83a99d-syringetest.apiary-mck.com/";


    private static Client instance;

    private HttpHolder mHttpHolder;

    public static synchronized Client getInstance() {

        if (null == instance) instance = new Client();
        return instance;
    }

    public Client() {

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(mOkHttpClient)
                .build();
        ServiceManager serviceManager = new ServiceManager(mRetrofit.create(com.zhj
                .example.service.IService.class), mRetrofit.create(TService.class));
        mHttpHolder = new HttpHolder(serviceManager);
    }

    public HttpHolder getHttpHolder() {

        return mHttpHolder;
    }

}
