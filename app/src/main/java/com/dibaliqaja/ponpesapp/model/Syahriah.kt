package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class SyahriahHistory(
    val date: Date,
    val month: String,
    val year: Int,
    val spp: Double
)

data class SyahriahHistoryResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("total_page") val totalPage: Int,
    @SerializedName("data") val data: ArrayList<SyahriahHistory>
)
