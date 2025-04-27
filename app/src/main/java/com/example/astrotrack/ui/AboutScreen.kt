package com.example.astrotrack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("About AstroTrack", style = MaterialTheme.typography.headlineMedium)

        Text(
            text = "AstroTrack is a space-themed Android app built using Jetpack Compose and Firebase. " +
                    "It allows users to view NASA's Astronomy Picture of the Day (APOD), save favorites, manage profiles, and more.",
            fontSize = 16.sp
        )

        Text(
            text = "Built by:  Group 07\nVersion: 1.0.0\nOpen source: https://github.com/t3komu00/Mobile-App-Group-7",
            fontSize = 14.sp
        )
    }
}
