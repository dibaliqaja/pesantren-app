package com.dibaliqaja.ponpesapp.helper

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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