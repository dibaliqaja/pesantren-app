package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName

data class PasswordResponse(@SerializedName("data") val data: Password)
data class Password(
    @SerializedName("current_password") var oldPassword: String,
    @SerializedName("password") var newPassword: String,
    @SerializedName("password_confirmation") var newPasswordConfirmation: String,
)