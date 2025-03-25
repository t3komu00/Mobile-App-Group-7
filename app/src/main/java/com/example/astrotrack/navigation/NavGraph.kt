package com.example.astrotrack.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.astrotrack.ui.DetailScreen
import com.example.astrotrack.ui.FavoritesScreen
import com.example.astrotrack.ui.MainScreen
import com.example.astrotrack.ui.components.BottomNavigationBar
import com.example.astrotrack.viewmodel.ApodViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, viewModel: ApodViewModel) {
    // Observe the selected APOD item from the ViewModel
    val selectedItem = viewModel.selectedItem.collectAsState().value

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("main") {
                MainScreen(viewModel, navController)
            }
            composable("favorites") {
                FavoritesScreen(viewModel, navController)
            }
            composable("detail/selected") {
                selectedItem?.let {
                    DetailScreen(it) //  This requires item: ApodItem in DetailScreen
                }
            }
        }
    }
}
