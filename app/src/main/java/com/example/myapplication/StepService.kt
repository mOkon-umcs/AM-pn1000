package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.example.myapplication.StepsNotifications.GOAL

class StepService: Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "steps_service_channel"
        private const val NOTIFICATION_ID = 2
        const val EXTRA_STEPS = "steps"
        private const val TICK_MS = 2000L

        fun start(context: Context, steps: Int){
            val intent = Intent(context, StepService::class.java)
                .putExtra(EXTRA_STEPS, steps)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context){
            context.stopService(Intent(context, StepService::class.java))
        }
    }

    private var steps = 0
    private val handler = Handler(Looper.getMainLooper())

    private val ticker = object : Runnable {
        override fun run() {
            steps++
            updateNotification()
            handler.postDelayed(this, TICK_MS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(CHANNEL_ID, "Krokomierz w tle", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        steps = intent?.getIntExtra(EXTRA_STEPS, steps) ?: steps
        startForeground(NOTIFICATION_ID, buildNotification())
        handler.removeCallbacks(ticker)
        handler.postDelayed(ticker, TICK_MS)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }

    private fun updateNotification(){
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Krokomierz")
            .setContentText(
                if(steps >= StepsNotifications.GOAL) "Cel osiągnięty: $steps kroków!!"
                else "Kroki: $steps z ${StepsNotifications.GOAL}"
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

}