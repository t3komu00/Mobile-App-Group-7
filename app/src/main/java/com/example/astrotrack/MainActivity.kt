package com.example.astrotrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.astrotrack.navigation.AppNavGraph
import com.example.astrotrack.ui.theme.AstroTrackTheme
import com.example.astrotrack.viewmodel.ApodViewModel
import com.example.astrotrack.worker.ReminderWorker
import com.google.firebase.FirebaseApp
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val viewModel: ApodViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Ask for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        // Schedule daily notification at 8:00 AM
        scheduleDailyReminder(hour = 8, minute = 0)

        // Theme-aware Compose UI
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            AstroTrackTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    // Schedule repeating daily notification using PeriodicWorkRequest
    private fun scheduleDailyReminder(hour: Int, minute: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder", // Unique name for the worker
            ExistingPeriodicWorkPolicy.REPLACE, // Replace if it already exists
            dailyWorkRequest
        )
    }

    // Helper: calculate delay until next desired trigger time
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DATE, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
