package me.daemon.storage

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

fun Context.saveImageToMediaStore(
    data: ByteArray,
    metadata: ImageMetaData,
): Uri? {
    log.d("saveImageToMediaStore: ${metadata.name}, ${data.size}, ${metadata.width}, ${metadata.height}")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, metadata.name)
        metadata.mimeType?.let { put(MediaStore.MediaColumns.MIME_TYPE, it) }
        put(MediaStore.Images.Media.ORIENTATION, metadata.orientation)
        metadata.latitude?.let { put(MediaStore.Images.Media.LATITUDE, it) }
        metadata.longitude?.let { put(MediaStore.Images.Media.LONGITUDE, it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveImageToMediaStore contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveImageToMediaStore openFileDescriptor failed")
        return null
    }
    descriptor.use { pfd ->
        FileOutputStream(pfd.fileDescriptor).write(data)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        detail.clear()
        detail.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(contentUri, detail, null, null)
    }
    return contentUri
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
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(MediaStore.Audio.Media.DISPLAY_NAME, metadata.name)
        if (metadata.latitude != null) {
            put(MediaStore.Images.Media.LATITUDE, metadata.latitude)
        }
        if (metadata.longitude != null) {
            put(MediaStore.Images.Media.LONGITUDE, metadata.longitude)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveAudioToMediaStore contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveAudioToMediaStore openFileDescriptor failed")
        return null
    }
    descriptor.use { pfd ->
        FileOutputStream(pfd.fileDescriptor).write(data)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        detail.clear()
        detail.put(MediaStore.Audio.Media.IS_PENDING, 0)
        resolver.update(contentUri, detail, null, null)
    }
    return contentUri
}

fun Context.saveVideoToMediaStore(
    name: String,
    data: ByteArray,
    width: Int,
    height: Int,
    latitude: Double? = null,
    longitude: Double? = null,
): Uri? {
    log.d("saveVideoToMediaStore: $name, ${data.size}, $width, $height")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, name)
        if (latitude != null) {
            put(MediaStore.Images.Media.LATITUDE, latitude)
        }
        if (longitude != null) {
            put(MediaStore.Images.Media.LONGITUDE, longitude)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveVideoToMediaStore contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveVideoToMediaStore openFileDescriptor failed")
        return null
    }
    descriptor.use { pfd ->
        FileOutputStream(pfd.fileDescriptor).write(data)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        detail.clear()
        detail.put(MediaStore.Video.Media.IS_PENDING, 0)
        resolver.update(contentUri, detail, null, null)
    }
    return contentUri
}
