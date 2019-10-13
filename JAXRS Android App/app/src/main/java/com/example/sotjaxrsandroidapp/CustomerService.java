package com.example.sotjaxrsandroidapp;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CustomerService {
    @GET("customers/all")
    Call<ArrayList<Customer>> getAllCustomer();

    @GET("customers")
    Call<Customer> findCustomer(@Query("id") int id);

    @PUT("customers")
    Call<Void> updateCustomer(@Body Customer newData);

    @DELETE("customers/{id}")
    Call<Void> deleteCustomer(@Path("id") int id);

    @POST("customers")
    @FormUrlEncoded
    Call<Void> createCustomer(@Field("name") String name, @Field("email") String email);
}
