package com.example.mywatertracker

import android.content.Context
import android.content.Intent
import kotlin.math.roundToInt

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Float.roundToDecimalPoints(places: Int): Float {
    var tempNumber = 1f
    for (i in 1..places) {
        tempNumber *= 10.0f
    }
    return (this * tempNumber).roundToInt().toFloat() / tempNumber
}

fun Context.startService() {
    startService(Intent(this, TrackerService::class.java).apply {
        action = TrackerService.ACTION_START
    })
}

fun Context.stopService() {
    startService(Intent(this, TrackerService::class.java).apply {
        action = TrackerService.ACTION_STOP
    })
}