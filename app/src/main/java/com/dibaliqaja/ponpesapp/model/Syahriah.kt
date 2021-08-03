package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class Syahriah(
    val date: Date,
    val month: String,
    val year: Int,
    val spp: Double
)

data class SyahriahHistoryResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("total_page") val totalPage: Int,
    @SerializedName("data") val data: ArrayList<Syahriah>
)

data class SyahriahSppResponse(
    @SerializedName("year") val year: Int,
    @SerializedName("data") val data: HashMap<String, Syahriah>
)
