package com.example.sotjaxrsandroidapp;

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

public interface GameService {
    @GET("games/available-games")
    Call<ArrayList<Game>> getAllGames();

    @GET("games")
    Call<Game> findGame(@Query("id") int id);

    @PUT("games")
    Call<Void> updateGame(@Body Game newData);

    @DELETE("games/{id}")
    Call<Void> deleteGame(@Path("id") int id);

    @POST("games")
    @FormUrlEncoded
    Call<Void> createGame(@Field("name") String name, @Field("genre") GameGenre genre, @Field("price") double price);
}
