package com.roman.mobidev.todolist.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by roman on 25.04.16.
 */
public class RetrofitHelper {

    private static RetrofitHelper instance;

    private Gson gson = new GsonBuilder().create();
    private  Retrofit retrofit;
    private  Api service;
    private  static  RetrofitHelper getInstance(){

        if (instance == null){
            instance = new RetrofitHelper();
        }
        return instance;
    }

    public static Api getApiService(){
        return getInstance().service;
    }

    private  RetrofitHelper(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://194.8.145.17:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(Api.class);
    }
}
