package com.dibaliqaja.ponpesapp.services

import com.dibaliqaja.ponpesapp.model.LoginRequest
import com.dibaliqaja.ponpesapp.model.LoginResponse
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("refresh")
    fun refresh(@Header("Authorization") authorization: String): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") authorization: String): Call<ProfileResponse>
}