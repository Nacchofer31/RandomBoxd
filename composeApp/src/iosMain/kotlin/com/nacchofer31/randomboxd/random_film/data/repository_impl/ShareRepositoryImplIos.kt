package com.nacchofer31.randomboxd.random_film.data.repository_impl

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import com.nacchofer31.randomboxd.random_film.domain.repository.ShareRepository
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIWindowScene

class ShareRepositoryImplIos : ShareRepository {
    override suspend fun shareImage(
        image: ImageBitmap,
        fileName: String,
    ) {
        withContext(Dispatchers.Main) {
            val skiaImage = Image.makeFromBitmap(image.asSkiaBitmap())
            val encoded = skiaImage.encodeToData(EncodedImageFormat.PNG) ?: return@withContext
            val uiImage = UIImage.imageWithData(encoded.toNSData()) ?: return@withContext
            val activityViewController =
                UIActivityViewController(
                    activityItems = listOf(uiImage),
                    applicationActivities = null,
                )
            val windowScene = UIApplication.sharedApplication.connectedScenes.anyObject() as? UIWindowScene
            val rootViewController =
                windowScene?.windows?.firstOrNull()?.rootViewController
                    ?: UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(activityViewController, animated = true, completion = null)
        }
    }

    private fun org.jetbrains.skia.Data.toNSData(): NSData {
        val bytes = this.bytes
        return bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
    }
}
