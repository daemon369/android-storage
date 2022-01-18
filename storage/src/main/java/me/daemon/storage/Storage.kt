package me.daemon.storage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import me.daemon.logger.getLogger
import java.io.FileOutputStream


private val log = getLogger(null)

val isExternalStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

val isExternalStorageReadable: Boolean
    get() = when (Environment.getExternalStorageState()) {
        Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY -> true
        else -> false
    }

fun Context.saveImageFile(
    name: String,
    data: ByteArray,
    width: Int,
    height: Int
): Uri? {
    log.d("saveImageFile: $name, ${data.size}, $width, $height")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(
            MediaStore.Images.Media.DISPLAY_NAME,
            name
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveImageFile contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveImageFile openFileDescriptor failed")
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

fun Context.saveAudioFile(
    name: String,
    data: ByteArray,
    width: Int,
    height: Int
): Uri? {
    log.d("saveAudioFile: $name, ${data.size}, $width, $height")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(
            MediaStore.Audio.Media.DISPLAY_NAME,
            name
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveAudioFile contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveAudioFile openFileDescriptor failed")
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

fun Context.saveVideoFile(
    name: String,
    data: ByteArray,
    width: Int,
    height: Int
): Uri? {
    log.d("saveVideoFile: $name, ${data.size}, $width, $height")
    val resolver = contentResolver
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
    val detail = ContentValues().apply {
        put(
            MediaStore.Video.Media.DISPLAY_NAME,
            name
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
    }
    val contentUri = resolver.insert(collection, detail)
    if (contentUri == null) {
        log.e("saveVideoFile contentUri is null")
        return null
    }
    val descriptor = resolver.openFileDescriptor(contentUri, "w", null)
    if (descriptor == null) {
        log.e("saveVideoFile openFileDescriptor failed")
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
