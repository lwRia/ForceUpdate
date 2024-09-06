package com.example.appupdate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

class DownloadImageTask(private val bmImage: ImageView) {
    fun execute(url: String?) {
        // Launch a coroutine to download the image and set it on the ImageView
        CoroutineScope(Dispatchers.Main).launch {
            setImageFromUrl(url)
        }
    }

    private suspend fun downloadImage(url: String?): Bitmap? {
        return try {
            url?.let {
                val inputStream: InputStream = withContext(Dispatchers.IO) {
                    URL(it).openStream()
                }
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("Error", e.message ?: "Unknown error")
            e.printStackTrace()
            null
        }
    }

    private suspend fun setImageFromUrl(url: String?) {
        val bitmap = downloadImage(url)
        bitmap?.let {
            bmImage.setImageBitmap(it)
        }
    }
}