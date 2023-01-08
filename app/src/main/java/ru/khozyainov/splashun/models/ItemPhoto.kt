package ru.khozyainov.splashun.models


data class ItemPhoto(
    override val id: String,
    val width: Int,
    val height: Int,
    val image: String,
    val like: Boolean,
    override val likes: Long,
    val author: Author
): DataForDiffUtil, Photo(id, likes)
