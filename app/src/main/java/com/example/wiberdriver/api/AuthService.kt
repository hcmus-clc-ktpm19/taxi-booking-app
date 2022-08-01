package com.example.wiberdriver.api

import com.example.wiberdriver.models.entity.AuthToken
import com.example.wiberdriver.models.entity.Account
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AuthService {

    @POST("auth/register")
    fun registerCustomer(@Body customer: Account): Call<ResponseBody>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginThroughAPI(@Field("phone") phone: String,@Field("password") password : String): Call<AuthToken>

    @GET("auth")
    suspend fun getAccountDetail(@Query("q") phoneNumber : String, @Header("Authorization") accessToken : String): Account

    @PUT("auth/{id}")
    suspend fun updatePasswordAPI(@Path("id") accountID : String, @Body accountNewPass : Account, @Header("Authorization") accessToken : String): ResponseBody

    companion object {
        private var url: String = "http://10.0.2.2:8080/api/v1/"
        val authService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java);
    }
}