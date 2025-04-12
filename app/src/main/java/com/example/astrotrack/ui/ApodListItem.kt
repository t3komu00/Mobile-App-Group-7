package com.example.astrotrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.astrotrack.model.ApodItem
import com.example.astrotrack.utils.downloadImage

@Composable
fun ApodListItem(
    item: ApodItem,
    onClick: () -> Unit,
    onFavoriteClick: (ApodItem, Boolean) -> Unit,
    isFavorite: Boolean
) {
    var favorited by remember { mutableStateOf(isFavorite) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            Column(modifier = Modifier.padding(12.dp)) {
                if (item.media_type == "image") {
                    AsyncImage(
                        model = item.url,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                } else {
                    Text("🎥 Video – Tap for details", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text(item.date, style = MaterialTheme.typography.bodySmall)
                    }

                    if (item.media_type == "image") {
                        IconButton(onClick = {
                            downloadImage(context, item.url, item.title.replace(" ", "_"))
                        }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Download")
                        }
                    }
                }
            }

            IconButton(
                onClick = {
                    favorited = !favorited
                    onFavoriteClick(item, favorited)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (favorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (favorited) Color.Red else Color.LightGray
                )
            }
        }
    }
}
