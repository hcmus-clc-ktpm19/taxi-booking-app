package com.example.wiberdriver.api

import com.example.wiberdriver.models.entity.CarRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CarRequestService {

    @POST("car-request/create-or-update")
    fun requestCarByAPI(@Body carRequestInfo: CarRequest, @Header("Authorization") accessToken : String): Call<ResponseBody>

    @POST("car-request/update-arriving-address")
    fun updateArrivingByAPI(@Body carRequestInfo: CarRequest, @Header("Authorization") accessToken : String): Call<ResponseBody>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val carRequestService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarRequestService::class.java);
    }

}