package com.dibaliqaja.ponpesapp.helper

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