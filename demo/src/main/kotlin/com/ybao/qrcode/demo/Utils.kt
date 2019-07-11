package com.ybao.qrcode.demo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.format.DateFormat
import java.io.File
import java.io.FileOutputStream
import java.util.*

object Utils {
    fun saveBitmap(context: Context, bitmap: Bitmap, path: String, bitName: String? = null, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): String {

        return saveBitmap(context, bitmap, File(path), bitName, format)
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, dir: File, bitName: String? = null, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): String {
        if (!dir.exists()) {
            dir.mkdir()
        }
        val fileName = "${if (bitName == null || bitName.isEmpty()) DateFormat.format("yyyy-MM-dd HH:mm:ss", Date()).toString() else bitName}.${format.name.toLowerCase()}"
        val file = File(dir, fileName)
        if (file.exists()) {
            file.delete()
        }
        FileOutputStream(file).use { out ->
            bitmap.compress(format, 100, out)
            out.flush()
        }
        return file.absolutePath
    }

    fun updatePhoto(context: Context, path: String) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, path)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        context.sendBroadcast(intent)
    }
}