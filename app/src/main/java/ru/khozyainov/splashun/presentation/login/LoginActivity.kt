package ru.khozyainov.splashun.presentation.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.utils.setStatusBarTextColor

@AndroidEntryPoint
class LoginActivity: AppCompatActivity(R.layout.activity_login){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTextColor()
    }
}