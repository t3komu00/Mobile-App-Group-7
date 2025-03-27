package com.example.astrotrack.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.astrotrack.ui.components.ApodListItem
import com.example.astrotrack.viewmodel.ApodViewModel

@Composable
fun FavoritesScreen(viewModel: ApodViewModel, navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val favorites by viewModel.favoriteList.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFavorites = if (searchQuery.isNotBlank())
        favorites.filter {
            it.title.contains(searchQuery, true) ||
                    it.explanation.contains(searchQuery, true) ||
                    it.date.contains(searchQuery, true) ||
                    (it.copyright?.contains(searchQuery, true) ?: false)
        }
    else favorites

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("My Favorites", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { showSearch = !showSearch }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search favorites...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(filteredFavorites) { item ->
                ApodListItem(
                    item = item,
                    isFavorite = true,
                    onClick = {
                        viewModel.setSelectedItem(item)
                        navController.navigate("detail/selected")
                    },
                    onFavoriteClick = { apod, isFav ->
                        viewModel.toggleFavorite(apod, isFav) { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
