package me.daemon.storage

data class Metadata(
    val name: String,
    val mimeType: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    class Builder {
        private var name: String? = null
        private var mimeType: String? = null
        private var latitude: Double? = null
        private var longitude: Double? = null

        fun name(name: String?) = apply { this.name = name }
        fun mimeType(mimeType: String?) = apply { this.mimeType = mimeType }
        fun latitude(latitude: Double?) = apply { this.latitude = latitude }
        fun longitude(longitude: Double?) = apply { this.longitude = longitude }

        fun build(): Metadata {
            return Metadata(
                name ?: throw IllegalArgumentException("name is empty"),
                mimeType,
                latitude,
                longitude,
            )
        }
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}