package ru.khozyainov.splashun.models

data class PhotoDetails(
    override val id: String,
    val width: Int,
    val height: Int,
    val placeholder: String,
    val image: String,
    val like: Boolean,
    override val likes: Long,
    val author: Author,
    val location: Location,
    val exif: Exif,
    val tags: List<String>,
    val downloads: Int
): DataForDiffUtil, Photo(id, likes)
