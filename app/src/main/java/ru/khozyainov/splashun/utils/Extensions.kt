package ru.khozyainov.splashun.utils

import android.content.Context
import android.content.res.Configuration
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable


fun Context.orientationIsPortrait(): Boolean
    = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun AppCompatActivity.setStatusBarTextColor(){
    val textColorIsBlack = when(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
        Configuration.UI_MODE_NIGHT_NO -> true
        Configuration.UI_MODE_NIGHT_YES -> false
        else -> true
    }
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = textColorIsBlack

}

fun Context.getCircularProgressDrawable(): CircularProgressDrawable =
    CircularProgressDrawable(this).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

fun Context.getDisplayWidthHeight(): Pair<Int, Int>
    = this.resources.displayMetrics.widthPixels to this.resources.displayMetrics.heightPixels

fun String.underLine(): SpannableString {
    val spanStr = SpannableString(this)
    spanStr.setSpan(UnderlineSpan(), 0, spanStr.length, 0)
    return spanStr
}

