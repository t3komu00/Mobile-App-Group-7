package com.example.astrotrack.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.astrotrack.model.ApodItem
import com.example.astrotrack.utils.downloadImage

@Composable
fun DetailScreen(item: ApodItem) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (item.media_type == "image") {
            AsyncImage(
                model = item.url,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            Text(
                text = "🎥 Open Video Link",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                        context.startActivity(intent)
                    }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(item.title, style = MaterialTheme.typography.titleLarge)
        Text("Date: ${item.date}", style = MaterialTheme.typography.bodySmall)
        Text("© ${item.copyright ?: "Public Domain"}", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))
        Text(item.explanation, style = MaterialTheme.typography.bodyMedium)

        if (item.media_type == "image") {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                downloadImage(context, item.url, item.title.replace(" ", "_"))
            }) {
                Icon(Icons.Default.Download, contentDescription = "Download")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Image")
            }
        }
    }
}
