package com.example.mywatertracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.*


class TrackerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val INTENT_FILTER = "INTENT_FILTER"
        var isMyServiceRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startNotification()
                isMyServiceRunning = true
            }

            ACTION_STOP -> {
                stop()
                isMyServiceRunning = false
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startNotificationOreo()
        else
            startNotificationOld()
    }

    private fun startNotificationOld() {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setOngoing(true)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("Daily Drink Target")
            setCategory(Notification.CATEGORY_SERVICE)
            setSilent(true)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        updateNotification(notificationManager, notificationBuilder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startNotificationOreo() {
        val notificationChannel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, MODULE_TIMER, NotificationManager.IMPORTANCE_NONE).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
        val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setOngoing(true)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("Daily Drink Target")
            setContentText(getContentText())
            priority = NotificationManager.IMPORTANCE_MIN
            setCategory(Notification.CATEGORY_SERVICE)
            setSilent(true)
        }
        updateNotification(notificationManager, notificationBuilder)
    }

    private fun updateNotification(notificationManager: NotificationManager, notificationBuilder: NotificationCompat.Builder) {
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        serviceScope.launch {
            while (true) {
                val updatedNotification = notificationBuilder.setContentText(getContentText())
                notificationManager.notify(NOTIFICATION_ID, updatedNotification.build())
                if (MainActivity.isMainActivityActive) {
                    val intent = Intent(INTENT_FILTER)
                    sendBroadcast(intent)
                }
                delay(5_000)
                config.currentWaterLevel -= DEFAULT_WATER_TO_REMOVE
            }
        }
    }

    private fun getContentText(): String{
        return "${config.currentWaterLevel.roundToDecimalPoints(2)} / ${DEFAULT_WATER_LEVEL.roundToDecimalPoints(2)} ml"
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}

