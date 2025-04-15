package com.example.astrotrack.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.advan.presentation.EditProfileScreen
import com.example.astrotrack.ui.*
import com.example.astrotrack.ui.components.AppScaffold
import com.example.astrotrack.viewmodel.ApodViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: ApodViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val selectedItem = viewModel.selectedItem.collectAsState().value

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, viewModel) }
        composable("signup") { SignUpScreen(navController) }

        composable("main") {
            AppScaffold(navController = navController) { padding ->
                MainScreen(
                    viewModel = viewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("favorites") {
            AppScaffold(navController = navController) { padding ->
                FavoritesScreen(
                    viewModel = viewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("profile") {
            AppScaffold(navController = navController) { padding ->
                ProfileScreen(
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("editProfile") {
            EditProfileScreen(navController)
        }

        composable("settings") {
            SettingsScreen(
                isDarkMode = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        composable("about") {
            AboutScreen()
        }

        composable("detail/selected") {
            selectedItem?.let {
                DetailScreen(it)
            }
        }
    }
}
