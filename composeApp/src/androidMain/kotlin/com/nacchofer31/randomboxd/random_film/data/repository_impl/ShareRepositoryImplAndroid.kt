package com.nacchofer31.randomboxd.random_film.data.repository_impl

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.nacchofer31.randomboxd.random_film.domain.repository.ShareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ShareRepositoryImplAndroid(
    private val context: Context,
) : ShareRepository {
    override suspend fun shareImage(
        image: ImageBitmap,
        fileName: String,
    ) {
        val bitmap = image.asAndroidBitmap()
        val safeFileName = sanitizeFileName(fileName)
        val uri =
            withContext(Dispatchers.IO) {
                val dir = File(context.cacheDir, "share")
                dir.mkdirs()
                val file = File(dir, "$safeFileName.png")
                file.outputStream().use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        val chooser = Intent.createChooser(intent, null).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(chooser)
    }

    private fun sanitizeFileName(fileName: String): String {
        val slug = fileName.substringAfterLast('/').trimEnd('/')
        val sanitized = slug.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_')
        return sanitized.ifBlank { "randomboxd" }
    }
}
