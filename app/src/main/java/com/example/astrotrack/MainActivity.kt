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
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import androidx.work.WorkManager
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

        //  Ask for POST_NOTIFICATIONS permission (Android 13+)
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

        //  Schedule testable local notification
        scheduleTestReminder(hour = 8, minute = 0)

        setContent {
            AstroTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }

    private fun scheduleTestReminder(hour: Int, minute: Int) {
        val current = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(current)) add(Calendar.DAY_OF_MONTH, 1)
        }

        val delayMillis = target.timeInMillis - current.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
