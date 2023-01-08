package ru.khozyainov.splashun.presentation.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.khozyainov.splashun.presentation.MainActivity
import ru.khozyainov.splashun.presentation.login.LoginActivity
import ru.khozyainov.splashun.presentation.onboarding.OnboardingActivity
import ru.khozyainov.splashun.utils.launchAndCollectForActivity
import ru.khozyainov.splashun.utils.setStatusBarTextColor

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarTextColor()

        val viewModel: LauncherViewModel by viewModels()

        viewModel.loginAndOnboardingCompleted.launchAndCollectForActivity(lifecycle) { action ->
            when (action) {
                is LauncherViewModel.LaunchNavigatonAction.NavigateToOnboardingAction ->
                    startActivity(Intent(this@LauncherActivity, OnboardingActivity::class.java))
                is LauncherViewModel.LaunchNavigatonAction.NavigateToLoginActivityAction ->
                    startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
                is LauncherViewModel.LaunchNavigatonAction.NavigateToMainActivityAction ->
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            }
            finish()
        }
    }
}