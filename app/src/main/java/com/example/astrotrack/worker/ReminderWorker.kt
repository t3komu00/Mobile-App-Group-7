package com.example.astrotrack.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.astrotrack.R
import com.example.astrotrack.model.ApodItem
import com.example.astrotrack.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val CHANNEL_NAME = "Daily Reminder"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val response = RetrofitInstance.api.getApodData(
                startDate = today,
                endDate = today
            )

            if (response.isNotEmpty()) {
                val apod: ApodItem = response[0]
                val message = apod.explanation.take(120) + "..."
                showNotification(apod.title, message)
            } else {
                showNotification("NASA APOD", "No photo found for today.")
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun showNotification(title: String, message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}
