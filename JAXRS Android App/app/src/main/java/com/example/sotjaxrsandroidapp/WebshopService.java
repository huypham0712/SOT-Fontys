package com.example.sotjaxrsandroidapp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebshopService {

    @GET("webshop/sell-game")
    Call<Void> sellGame(@Query("cusId") int cusId, @Query("gameId") int gameId);

    @POST("webshop/add-balance")
    @FormUrlEncoded
    Call<Double> addBalance(@Field("cusId") int cusId, @Field("amount") double amount);
}
