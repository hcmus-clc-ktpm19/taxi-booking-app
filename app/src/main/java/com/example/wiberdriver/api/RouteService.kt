package com.example.wiberdriver.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteService {

    // "?api_key=5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573"
    @GET("v2/directions/driving-car")
    fun getPolyline(
        @Query("api_key") api_key: String,
        @Query("start") startPont: String,
        @Query("end") endPoint: String,
    ): Call<ResponseBody>

    companion object {
        private var url: String = "https://api.openrouteservice.org/"
        val routeService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouteService::class.java);
    }
}