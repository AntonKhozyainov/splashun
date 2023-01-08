package ru.khozyainov.splashun.models

import android.widget.TextView

abstract class Photo(
    open val id: String,
    open val likes: Long
){
    fun showLikeCount(likeCountTextView: TextView) {
        likeCountTextView.text = when {
            likes >= 1000000 -> "${
                likes.toString().substring(0, likes.toString().length - 6)
            }kk"
            likes >= 1000 -> "${
                likes.toString().substring(0, likes.toString().length - 3)
            }k"
            else -> likes.toString()
        }
    }
}