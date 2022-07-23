package com.example.wiberdriver.api

import com.example.wiberdriver.models.DriverInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface DriverService {

    @GET("driver/{phone}")
    fun getAPIDriverInfo(@Path("phone") phoneNumber : String, @Header("Authorization") accessToken : String): Call<DriverInfo>

    @POST("driver/create-or-update")
    fun updateDriverInfoAPI(@Body info: DriverInfo, @Header("Authorization") accessToken : String): Call<ResponseBody>

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val driverService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DriverService::class.java);
    }
}