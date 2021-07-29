package com.dibaliqaja.ponpesapp.services

import com.dibaliqaja.ponpesapp.model.LoginRequest
import com.dibaliqaja.ponpesapp.model.LoginResponse
import com.dibaliqaja.ponpesapp.model.PasswordResponse
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("refresh")
    fun refresh(@Header("Authorization") authorization: String): Call<LoginResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") authorization: String): Call<ProfileResponse>

    @Multipart
    @POST("profile")
    fun postUpdateProfile(@Header("Authorization") authorization: String,
                          @Part("name") name: RequestBody,
                          @Part("address") address: RequestBody,
                          @Part("birth_place") birthPlace: RequestBody,
                          @Part("birth_date") birthDate: RequestBody,
                          @Part("phone") phone: RequestBody,
                          @Part("school_old") schoolOld: RequestBody,
                          @Part("school_address_old") schoolAddressOld: RequestBody,
                          @Part("school_current") schoolCurrent: RequestBody,
                          @Part("school_address_current") schoolAddressCurrent: RequestBody,
                          @Part("father_name") fatherName: RequestBody,
                          @Part("mother_name") motherName: RequestBody,
                          @Part("father_job") fatherJob: RequestBody,
                          @Part("mother_job") motherJob: RequestBody,
                          @Part("parent_phone") parentPhone: RequestBody,
                          @Part("entry_year") entryYear: RequestBody,
                          @Part("year_out") yearOut: RequestBody?,
                          @Part image: MultipartBody.Part?
    ): Call<ProfileResponse>

    @FormUrlEncoded
    @PATCH("password")
    fun updatePassword(@Header("Authorization") authorization: String,
                       @Field("current_password") oldPassword: String,
                       @Field("password") newPassword : String,
                       @Field("password_confirmation") newPasswordConfirmation : String,
    ): Call<PasswordResponse>
}