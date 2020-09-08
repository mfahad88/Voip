package com.example.voip;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/Vokka/getclientbalance.do")
    Call<JsonElement> getclientbalance(@Query("pin")String mobileNo);
}
