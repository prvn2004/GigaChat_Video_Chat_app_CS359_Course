package com.example.gigachat.utlilities.imageUtilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class ImageEditors(val context: Context) {
    fun resizeImage(selectedImageUri: Uri): Bitmap {
        val imageStream = context.contentResolver.openInputStream(selectedImageUri)
        val originalBitmap = BitmapFactory.decodeStream(imageStream)

        // Calculate the desired dimensions to achieve a file size less than 100KB.
        val maxFileSize = 50 * 1024 // 100KB in bytes
        val compressionQuality = 70 // Adjust the compression quality as needed.

        val outputStream = ByteArrayOutputStream()
        var currentQuality = compressionQuality

        do {
            outputStream.reset()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)
            currentQuality -= 5 // Decrease the quality by 5 units each time.

        } while (outputStream.toByteArray().size > maxFileSize && currentQuality > 0)

        val resizedBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        return resizedBitmap
    }


}