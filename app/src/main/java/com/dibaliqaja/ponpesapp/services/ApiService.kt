package com.dibaliqaja.ponpesapp.services

import com.dibaliqaja.ponpesapp.model.LoginRequest
import com.dibaliqaja.ponpesapp.model.LoginResponse
import com.dibaliqaja.ponpesapp.model.PasswordResponse
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("refresh")
    fun refresh(@Header("Authorization") authorization: String): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") authorization: String): Call<ProfileResponse>

    @FormUrlEncoded
    @PATCH("password")
    fun updatePassword(@Header("Authorization") authorization: String,
                       @Field("current_password") oldPassword: String,
                       @Field("password") newPassword : String,
                       @Field("password_confirmation") newPasswordConfirmation : String,
    ): Call<PasswordResponse>
}