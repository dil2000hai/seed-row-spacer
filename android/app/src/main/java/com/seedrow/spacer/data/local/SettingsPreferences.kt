package com.seedrow.spacer.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readString(key: String, fallback: String): String =
        prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readLong(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun writeLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    companion object {
        private const val STORE = "seed_row_settings"
        const val KEY_UNITS = "units"
        const val KEY_DEFAULT_CROP = "default_crop"
        const val KEY_FROST = "frost"
    }
}
