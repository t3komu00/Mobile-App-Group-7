package com.example.astrotrack.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

fun downloadImage(context: Context, imageUrl: String, title: String = "astro_apod") {
    val request = DownloadManager.Request(Uri.parse(imageUrl))
        .setTitle(title)
        .setDescription("Downloading APOD image...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$title.jpg")
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}
