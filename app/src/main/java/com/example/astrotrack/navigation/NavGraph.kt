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
import com.example.astrotrack.ui.*
import com.example.astrotrack.ui.components.BottomNavigationBar
import com.example.astrotrack.viewmodel.ApodViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, viewModel: ApodViewModel) {
    val selectedItem = viewModel.selectedItem.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable("signup") {
            SignUpScreen(navController = navController)
        }

        composable("main") {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                MainScreen(
                    viewModel = viewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("favorites") {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                FavoritesScreen(
                    viewModel = viewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("profile") {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                ProfileScreen(
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("editProfile") {
            EditProfileScreen(navController)
        }

        composable("detail/selected") {
            selectedItem?.let {
                DetailScreen(it)
            }
        }
    }
}
