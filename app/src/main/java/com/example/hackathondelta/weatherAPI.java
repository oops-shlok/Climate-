package com.example.hackathondelta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface weatherAPI {
    @GET("weather")
    Call<DataClass> getweather(@Query("q") String cityname, @Query("appid") String apikey);

}
