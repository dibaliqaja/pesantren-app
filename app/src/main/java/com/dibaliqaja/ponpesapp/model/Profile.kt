package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ProfileResponse(@SerializedName("data") val data: Profile)

data class Profile(
    @SerializedName("email") val email : String,
    @SerializedName("role") val role : String,
    @SerializedName("santri_id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("address") val address : String,
    @SerializedName("birth_place") val birthPlace : String,
    @SerializedName("birth_date") val birthDate : Date,
    @SerializedName("phone") val phone : String,
    @SerializedName("school_old") val schoolOld : String,
    @SerializedName("school_address_old") val schoolAddressOld : String,
    @SerializedName("school_current") val schoolCurrent : String,
    @SerializedName("school_address_current") val schoolAddressCurrent : String,
    @SerializedName("father_name") val fatherName : String,
    @SerializedName("mother_name") val motherName : String,
    @SerializedName("father_job") val fatherJob : String,
    @SerializedName("mother_job") val motherJob : String,
    @SerializedName("parent_phone") val parentPhone : String,
    @SerializedName("entry_year") val entryYear : Int,
    @SerializedName("year_out") val yearOut : Int?,
    @SerializedName("photo") val photo : String?,
)