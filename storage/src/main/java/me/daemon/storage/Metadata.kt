@file:Suppress("unused")

package me.daemon.storage

import android.content.ContentValues
import android.provider.MediaStore

open class Metadata(
    val name: String,
    val mimeType: String? = null,
) {

    open fun contentValues() = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        mimeType?.let { put(MediaStore.MediaColumns.MIME_TYPE, it) }
    }

    @Suppress("UNCHECKED_CAST")
    abstract class Builder<T : Builder<T, M>, M : Metadata> {
        protected var name: String? = null
        protected var mimeType: String? = null

        fun name(name: String?): T {
            this.name = name
            return this as T
        }

        fun mimeType(mimeType: String?): T {
            this.mimeType = mimeType
            return this as T
        }

        abstract fun build(): M
    }

}

class ImageMetaData(
    name: String,
    val width: Int,
    val height: Int,
    @Orientation val orientation: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    mimeType: String? = null,
) : Metadata(name, mimeType) {

    companion object {
        @JvmStatic
        fun builder(): ImageBuilder = ImageBuilder()
    }

    override fun contentValues(): ContentValues =
        super.contentValues().apply {
            put(MediaStore.Images.Media.ORIENTATION, orientation)
            latitude?.let { put(MediaStore.Images.Media.LATITUDE, it) }
            longitude?.let { put(MediaStore.Images.Media.LONGITUDE, it) }
        }

    class ImageBuilder : Builder<ImageBuilder, ImageMetaData>() {
        private var width: Int = 0
        private var height: Int = 0
        private var latitude: Double? = null
        private var longitude: Double? = null

        @Orientation
        private var orientation: Int = 0

        fun width(width: Int) = apply { this.width = width }
        fun height(height: Int) = apply { this.height = height }
        fun orientation(@Orientation orientation: Int) = apply { this.orientation = orientation }
        fun latitude(latitude: Double?) = apply { this.latitude = latitude }
        fun longitude(longitude: Double?) = apply { this.longitude = longitude }

        override fun build(): ImageMetaData =
            ImageMetaData(
                name ?: throw IllegalArgumentException("name is empty"),
                width,
                height,
                orientation,
                latitude,
                longitude,
                mimeType,
            )
    }
}

class AudioMetadata(
    name: String,
    mimeType: String? = null,
) : Metadata(name, mimeType) {

    companion object {
        @JvmStatic
        fun builder(): AudioBuilder = AudioBuilder()
    }

    class AudioBuilder : Builder<AudioBuilder, AudioMetadata>() {

        override fun build(): AudioMetadata =
            AudioMetadata(
                name ?: throw IllegalArgumentException("name is empty"),
                mimeType,
            )
    }
}

class VideoMetadata(
    name: String,
    val width: Int,
    val height: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    mimeType: String? = null,
) : Metadata(name, mimeType) {

    companion object {
        @JvmStatic
        fun builder(): VideoBuilder = VideoBuilder()
    }

    override fun contentValues(): ContentValues =
        super.contentValues().apply {
            latitude?.let { put(MediaStore.Video.Media.LATITUDE, it) }
            longitude?.let { put(MediaStore.Video.Media.LONGITUDE, it) }
        }

    class VideoBuilder : Metadata.Builder<VideoBuilder, VideoMetadata>() {
        private var width: Int = 0
        private var height: Int = 0
        private var latitude: Double? = null
        private var longitude: Double? = null

        fun width(width: Int) = apply { this.width = width }
        fun height(height: Int) = apply { this.height = height }
        fun latitude(latitude: Double?) = apply { this.latitude = latitude }
        fun longitude(longitude: Double?) = apply { this.longitude = longitude }

        override fun build(): VideoMetadata =
            VideoMetadata(
                name ?: throw IllegalArgumentException("name is empty"),
                width,
                height,
                latitude,
                longitude,
                mimeType,
            )
    }
}