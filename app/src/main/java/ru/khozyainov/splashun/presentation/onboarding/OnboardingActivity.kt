package ru.khozyainov.splashun.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.presentation.login.LoginActivity

@AndroidEntryPoint
class OnboardingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val viewModel: OnboardingViewModel by viewModels()

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val wormIndicator: WormDotsIndicator = findViewById(R.id.wormIndicator)

        viewPager.adapter  = OnboardingAdapter(object : OnboardingAdapter.Listener{
            override fun showNextPage(onboardingCompleted: Boolean) {
                if (onboardingCompleted){
                    viewModel.onboardingCompleted()
                    startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
                    finish()
                }
                else viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
            override fun showPrevPage() {
                viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            }
        })
        wormIndicator.attachTo(viewPager)
    }
}