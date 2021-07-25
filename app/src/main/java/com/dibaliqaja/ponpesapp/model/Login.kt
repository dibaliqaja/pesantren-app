package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("data") val data: Login)
data class Login(@SerializedName("access_token") var token: String)
data class LoginRequest(var email: String?, var password: String?)
