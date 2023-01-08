package ru.khozyainov.splashun.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingItem(
    @StringRes val title: Int,
    @DrawableRes val image: Int
)
