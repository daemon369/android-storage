@file:Suppress("unused")

package me.daemon.storage

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.IntDef
import me.daemon.logger.getLogger
import java.io.FileOutputStream

private val log = getLogger()

@IntDef(value = [0, 90, 180, 270])
annotation class Orientation

val imageContentUri: Uri by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}

val videoContentUri: Uri by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }
}

private inline fun pending(
    resolver: ContentResolver,
    collection: Uri,
    contentValues: ContentValues,
    crossinline block: (contentUri: Uri) -> Uri?
): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    var contentUri = resolver.insert(collection, contentValues)
    if (contentUri == null) {
        log.e("contentUri is null")
        return null
    }
    contentUri = block(contentUri)
    if (contentUri == null) {
        return null
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(contentUri, contentValues, null, null)
    }
    return contentUri
}

fun Context.saveImageToMediaStore(
    data: ByteArray,
    metadata: ImageMetaData,
): Uri? {
    log.d("saveImageToMediaStore: ${metadata.name}, ${data.size}, ${metadata.width}, ${metadata.height}")
    val resolver = contentResolver
    val detail = metadata.contentValues()
    return pending(
        resolver,
        imageContentUri,
        detail
    ) {
        val descriptor = resolver.openFileDescriptor(it, "w", null)
        if (descriptor == null) {
            log.e("saveImageToMediaStore openFileDescriptor failed")
            return@pending null
        }
        descriptor.use { pfd ->
            FileOutputStream(pfd.fileDescriptor).write(data)
        }
        return@pending it
    }
}

fun Context.saveImageToMediaStore(
    name: String,
    data: ByteArray,
    width: Int,
    height: Int,
    mimeType: String? = null,
    @Orientation orientation: Int = 0,
    latitude: Double? = null,
    longitude: Double? = null,
): Uri? =
    saveImageToMediaStore(
        data,
        ImageMetaData
            .builder()
            .name(name)
            .mimeType(mimeType)
            .latitude(latitude)
            .longitude(longitude)
            .width(width)
            .height(height)
            .orientation(orientation)
            .build(),
    )

fun Context.saveAudioToMediaStore(
    data: ByteArray,
    metadata: AudioMetadata
): Uri? {
    log.d("saveAudioToMediaStore: ${metadata.name}, ${data.size}")
    val resolver = contentResolver
    val detail = metadata.contentValues()
    return pending(
        resolver,
        videoContentUri,
        detail
    ) {
        val descriptor = resolver.openFileDescriptor(it, "w", null)
        if (descriptor == null) {
            log.e("saveAudioToMediaStore openFileDescriptor failed")
            return@pending null
        }
        descriptor.use { pfd ->
            FileOutputStream(pfd.fileDescriptor).write(data)
        }
        return@pending it
    }
}

fun Context.saveVideoToMediaStore(
    data: ByteArray,
    metadata: VideoMetadata,
): Uri? {
    log.d("saveVideoToMediaStore: ${metadata.name}, ${data.size}, ${metadata.width}, ${metadata.height}")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    val detail = metadata.contentValues()
    return pending(
        resolver,
        collection,
        detail
    ) {
        val descriptor = resolver.openFileDescriptor(it, "w", null)
        if (descriptor == null) {
            log.e("saveVideoToMediaStore openFileDescriptor failed")
            return@pending null
        }
        descriptor.use { pfd ->
            FileOutputStream(pfd.fileDescriptor).write(data)
        }

        return@pending it
    }
}
