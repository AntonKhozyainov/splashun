package ru.khozyainov.splashun.models

interface DataForDiffUtil {
    val id: String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}