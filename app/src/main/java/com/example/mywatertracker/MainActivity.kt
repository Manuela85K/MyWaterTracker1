package com.example.mywatertracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mywatertracker.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isClicked = false

    companion object {
        var isMainActivityActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!TrackerService.isMyServiceRunning) {
            config.currentWaterLevel = DEFAULT_WATER_LEVEL
            startService()
        }
        registerReceiver(myReceiver, IntentFilter(TrackerService.INTENT_FILTER))

        binding.apply {
            llAddWater.setOnClickListener {
                if (!isClicked) {
                    isClicked = true
                    lifecycleScope.launch {
                        stopService()
                        config.currentWaterLevel += 200
                        setViews()
                        delay(300)
                        startService()
                        isClicked = false
                    }
                }
            }
        }
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setViews()
        }
    }

    override fun onResume() {
        super.onResume()
        isMainActivityActive = true
    }

    override fun onPause() {
        super.onPause()
        isMainActivityActive = false
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    private fun getProgress(): Int {
        val progress = ((config.currentWaterLevel / DEFAULT_WATER_LEVEL) * 100).roundToInt()
        return if (progress <= 100)
            progress
        else
            100
    }

    private fun setViews() {
        binding.apply {
            tvDefaultWaterLevel.text = DEFAULT_WATER_LEVEL.roundToInt().toString()
            tvWaterLevel.text = config.currentWaterLevel.roundToDecimalPoints(2).toString()
            binding.progressBar.progress = getProgress()
        }
    }
}