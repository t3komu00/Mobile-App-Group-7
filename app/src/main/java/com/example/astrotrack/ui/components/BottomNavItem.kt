package com.example.astrotrack.ui.components

sealed class BottomNavItem(val route: String, val label: String) {
    object Home : BottomNavItem("main", "Home")
    object Favorites : BottomNavItem("favorites", "Favorites")
}
