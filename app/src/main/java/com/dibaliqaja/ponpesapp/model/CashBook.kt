package com.dibaliqaja.ponpesapp.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class CashBook(
    val date: Date,
    val note: String,
    val debit: Double,
    val credit: Double
)
data class CashBookResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("total_page") val totalPage: Int,
    @SerializedName("saldo") val saldo: String,
    @SerializedName("data") val data: ArrayList<CashBook>
)