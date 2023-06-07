package com.example.mywatertracker

import android.content.Context
import android.preference.PreferenceManager

class Config(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var currentWaterLevel: Float
        get() = prefs.getFloat(CURRENT_WATER_LEVEL, 0f)
        set(value) {
            if (value > 0)
                prefs.edit().putFloat(CURRENT_WATER_LEVEL, value).apply()
            else
                prefs.edit().putFloat(CURRENT_WATER_LEVEL, 0f).apply()
        }

}
