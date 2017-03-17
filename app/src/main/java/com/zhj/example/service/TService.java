package com.zhj.example.service;

import rx.Observable;
import syringe.Service;
/**
 * Created by Fred Zhao on 2017/3/1.
 */
@Service
public interface TService {

    Observable<String> test();
}
