package ru.khozyainov.splashun.models

data class Exif(
    val made: String,
    val model: String,
    val exposure: String,
    val aperture: Double,
    val focalLength: Double,
    val iso: Int,
)