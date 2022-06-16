@file:Suppress("unused")

package me.daemon.storage

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.annotation.IntDef
import me.daemon.logger.getLogger
import java.io.FileOutputStream

private val log = getLogger()

@IntDef(value = [0, 90, 180, 270])
annotation class Orientation

val imageContentUri: Uri by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
}

val audioContentUri: Uri by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
}

val videoContentUri: Uri by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
}

private var fileContentUri: Uri? = null

private val Context.fileContentUri: Uri
    get() {
        val uri = me.daemon.storage.fileContentUri
        if (uri != null) return uri
        val volume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore
                .getExternalVolumeNames(this)
                .firstOrNull()
                ?: MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            "external"
        }
        return MediaStore
            .Files
            .getContentUri(volume)
            .apply { me.daemon.storage.fileContentUri = this }
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
    contentUri = block(contentUri) ?: return null

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

fun Context.saveAudioToMediaStore(
    data: ByteArray,
    metadata: AudioMetadata
): Uri? {
    log.d("saveAudioToMediaStore: ${metadata.name}, ${data.size}")
    val resolver = contentResolver
    val detail = metadata.contentValues()
    return pending(
        resolver,
        audioContentUri,
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
    block: ContentValues.() -> Unit = {}
): Uri? {
    log.d("saveVideoToMediaStore: ${metadata.name}, ${data.size}, ${metadata.width}, ${metadata.height}")
    val resolver = contentResolver
    val detail = metadata.contentValues()
    detail.block()
    return pending(
        resolver,
        videoContentUri,
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

fun Context.openFileDescriptor(uri: Uri): ParcelFileDescriptor? =
    contentResolver.openFileDescriptor(uri, "w", null)

fun <T : Metadata> Context.openMedia(
    metadata: T,
    block: ContentValues.() -> Unit = {},
): Uri? {
    val contentValues = metadata.contentValues()
    contentValues.block()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    val contentUri = when (metadata) {
        is ImageMetaData -> imageContentUri
        is VideoMetadata -> videoContentUri
        is AudioMetadata -> audioContentUri
        is FileMetadata -> fileContentUri
        else -> throw IllegalStateException("Unsupported metadata: $metadata")
    }
    return contentResolver.insert(contentUri, contentValues)
}

fun Context.openImage(
    metadata: ImageMetaData,
    block: ContentValues.() -> Unit = {},
): Uri? =
    openMedia(metadata, block)

fun Context.openVideo(
    metadata: VideoMetadata,
    block: ContentValues.() -> Unit = {},
): Uri? =
    openMedia(metadata, block)

fun Context.openAudio(
    metadata: AudioMetadata,
    block: ContentValues.() -> Unit = {},
): Uri? =
    openMedia(metadata, block)

fun Context.openFile(
    metadata: FileMetadata,
    block: ContentValues.() -> Unit = {},
): Uri? =
    openMedia(metadata, block)

fun Context.closeMedia(uri: Uri) {
    val contentValues = ContentValues()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
    }
    contentResolver.update(uri, contentValues, null, null)
}