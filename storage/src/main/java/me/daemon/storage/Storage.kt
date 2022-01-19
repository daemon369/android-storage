package me.daemon.storage

import android.os.Environment
import me.daemon.logger.getLogger


private val log = getLogger(null)

val isExternalStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

val isExternalStorageReadable: Boolean
    get() = when (Environment.getExternalStorageState()) {
        Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY -> true
        else -> false
    }
