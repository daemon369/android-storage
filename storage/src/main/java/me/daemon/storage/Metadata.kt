package me.daemon.storage

open class Metadata(
    val name: String,
    val mimeType: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    @Suppress("UNCHECKED_CAST")
    abstract class Builder<T : Builder<T, M>, M : Metadata> {
        protected var name: String? = null
        protected var mimeType: String? = null
        protected var latitude: Double? = null
        protected var longitude: Double? = null

        fun name(name: String?): T {
            this.name = name
            return this as T
        }

        fun mimeType(mimeType: String?): T {
            this.mimeType = mimeType
            return this as T
        }

        fun latitude(latitude: Double?): T {
            this.latitude = latitude
            return this as T
        }

        fun longitude(longitude: Double?): T {
            this.longitude = longitude
            return this as T
        }

        abstract fun build(): M
    }

}

class ImageMetaData(
    name: String,
    mimeType: String? = null,
    latitude: Double? = null,
    longitude: Double? = null,
    val width: Int,
    val height: Int,
    @Orientation val orientation: Int = 0,
) : Metadata(name, mimeType, latitude, longitude) {

    companion object {
        @JvmStatic
        fun builder(): ImageBuilder = ImageBuilder()
    }

    class ImageBuilder : Builder<ImageBuilder, ImageMetaData>() {
        private var width: Int = 0
        private var height: Int = 0

        @Orientation
        private var orientation: Int = 0

        fun width(width: Int) = apply { this.width = width }
        fun height(height: Int) = apply { this.height = height }
        fun orientation(@Orientation orientation: Int) = apply { this.orientation = orientation }

        override fun build(): ImageMetaData =
            ImageMetaData(
                name ?: throw IllegalArgumentException("name is empty"),
                mimeType,
                latitude,
                longitude,
                width,
                height,
                orientation
            )
    }
}

class AudioMetadata(
    name: String,
    mimeType: String? = null,
    latitude: Double? = null,
    longitude: Double? = null
) : Metadata(name, mimeType, latitude, longitude) {

    class AudioBuilder : Builder<AudioBuilder, AudioMetadata>() {

        override fun build(): AudioMetadata =
            AudioMetadata(
                name ?: throw IllegalArgumentException("name is empty"),
                mimeType,
                latitude,
                longitude,
            )
    }
}