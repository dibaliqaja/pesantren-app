package com.dibaliqaja.ponpesapp.helper

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefName = "prefs_login"
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun put(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun clear() {
        editor.clear().apply()
    }
}