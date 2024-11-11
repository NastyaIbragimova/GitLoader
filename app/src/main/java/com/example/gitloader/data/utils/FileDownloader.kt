package com.example.gitloader.data.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.gitloader.R
import okhttp3.ResponseBody
import java.io.File
import java.io.OutputStream

class FileDownloader(private val context: Context) {
    companion object {
        const val FILE_DOWNLOADING = "FILE_DOWNLOADING"
        const val FILE_DOWNLOADED = "FILE_DOWNLOADED"
    }

    fun saveFileToDownloads(
        responseBody: ResponseBody, fileName: String, onComplete: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileScoped(responseBody, fileName, onComplete, onError)
        } else {
            saveFileLegacy(responseBody, fileName, onComplete, onError)
        }
    }

    private fun saveFileLegacy(
        responseBody: ResponseBody,
        fileName: String,
        onComplete: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var file = File(downloadsDir, fileName)
        var version = 1
        while (file.exists()) {
            val nameWithoutExtension = fileName.substringBeforeLast(".")
            val extension = fileName.substringAfterLast(".", "")
            val newFileName = if (extension.isNotEmpty()) {
                "$nameWithoutExtension($version).$extension"
            } else {
                "$nameWithoutExtension($version)"
            }
            file = File(downloadsDir, newFileName)
            version++
        }
        saveToFile(responseBody, file.outputStream(), onComplete, onError)
    }

    private fun saveFileScoped(
        responseBody: ResponseBody,
        fileName: String,
        onComplete: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val resolver = context.contentResolver
        var uniqueFileName = fileName
        var version = 1

        while (fileExists(resolver, uniqueFileName)) {
            val nameWithoutExtension = fileName.substringBeforeLast(".")
            val extension = fileName.substringAfterLast(".", "")
            uniqueFileName = if (extension.isNotEmpty()) {
                "$nameWithoutExtension($version).$extension"
            } else {
                "$nameWithoutExtension($version)"
            }
            version++
        }
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        try {
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    saveToFile(responseBody, outputStream, onComplete, onError)
                }
            } ?: onError(Exception(context.getString(R.string.file_downloader_error)))
        } catch (e: Exception) {
            onError(Exception(context.getString(R.string.file_downloader_error)))
        }
    }

    private fun fileExists(resolver: ContentResolver, fileName: String): Boolean {
        val projection =
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.RELATIVE_PATH)
        val selection =
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(fileName, "${Environment.DIRECTORY_DOWNLOADS}/")

        resolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.count > 0
        }
        return false
    }

    private fun saveToFile(
        responseBody: ResponseBody,
        outputStream: OutputStream,
        onComplete: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        try {
            val inputStream = responseBody.byteStream()
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            onComplete()

        } catch (e: Exception) {
            onError(e)
        }
    }
}