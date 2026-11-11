package com.seedrow.spacer.data.local

import android.content.Context
import android.content.SharedPreferences

class GardenPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readString(key: String, fallback: String): String =
        prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readLong(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun writeLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    companion object {
        private const val STORE = "seed_row_garden"
        const val KEY_BEDS = "beds"
        const val KEY_SELECTED_BED = "selected_bed"
        const val KEY_CROP = "crop"
        const val KEY_IN_ROW = "in_row"
        const val KEY_BETWEEN_ROW = "between_row"
        const val KEY_GRID_KIND = "grid_kind"
        const val KEY_SOWN = "sown"
        const val KEY_SEASONS = "seasons"
        const val KEY_STRIPS = "strips"
        const val KEY_LAST_PLAN = "last_plan"
    }
}
