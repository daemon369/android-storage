package me.daemon.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import me.daemon.logger.getLogger
import java.io.File


private val log = getLogger()

val isExternalStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

val isExternalStorageReadable: Boolean
    get() = when (Environment.getExternalStorageState()) {
        Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY -> true
        else -> false
    }

val File.isExternalStorageWritable: Boolean
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = Environment.getExternalStorageState(this) == Environment.MEDIA_MOUNTED

val File.isExternalStorageReadable: Boolean
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = when (Environment.getExternalStorageState(this)) {
        Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY -> true
        else -> false
    }

fun Context.saveToFile(
    data: ByteArray,
    name: String,
    type: String?,
    internal: Boolean = false
): File {
    val dir =
        if (internal)
            filesDir
        else
            getExternalFilesDirs(type).find {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.isExternalStorageWritable
                } else {
                    true
                }
            } ?: filesDir

    val file = File(dir, name)
    file.writeBytes(data)
    return file
}

fun Context.saveImageToFile(data: ByteArray, name: String, internal: Boolean = false): File =
    saveToFile(data, name, Environment.DIRECTORY_PICTURES, internal)

fun Context.saveVideoToFile(data: ByteArray, name: String, internal: Boolean = false): File =
    saveToFile(data, name, Environment.DIRECTORY_DCIM, internal)

fun Context.saveAudioToFile(data: ByteArray, name: String, internal: Boolean = false): File =
    saveToFile(data, name, Environment.DIRECTORY_MUSIC, internal)
