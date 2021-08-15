package com.dibaliqaja.ponpesapp.helper

import android.util.Log
import com.dibaliqaja.ponpesapp.services.ApiService
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import java.net.URI
import java.net.URL

internal class FormattedDateMatcher : DateMatcher {
    override fun matches(date: String?): Boolean {
        return DATE_PATTERN.matcher(date!!).matches()
    }

    companion object {
        private val DATE_PATTERN: Pattern = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}$"
        )
    }
}

interface DateMatcher {
    fun matches(date: String?): Boolean
}

fun rupiah(number: Double): String{
    val localeID =  Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    return numberFormat.format(number).toString()
}

fun formatDate(date: Date): String {
    return SimpleDateFormat("d MMMM yyyy", Locale("id")).format(date)
}
